package com.repent.app.ui.screens.checkout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.repent.app.data.local.CartStore
import com.repent.app.data.model.CartItem
import com.repent.app.data.model.CheckoutItem
import com.repent.app.data.repository.ApiResult
import com.repent.app.data.repository.OrderRepository
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val orderRepository: OrderRepository,
    private val cartStore: CartStore,
) : ViewModel() {

    var isSubmitting by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun placeOrder(
        cartItems: List<CartItem>,
        address: String,
        city: String,
        postalCode: String,
        phone: String,
        paymentMethod: String,
        onSuccess: (orderId: Int, total: Double) -> Unit,
    ) {
        errorMessage = null
        if (address.isBlank() || city.isBlank() || postalCode.isBlank() || phone.isBlank()) {
            errorMessage = "Please fill in all delivery details."
            return
        }

        viewModelScope.launch {
            isSubmitting = true
            val items = cartItems.map { CheckoutItem(it.productId, it.quantity) }
            when (val result = orderRepository.checkout(items, address, city, postalCode, phone, paymentMethod)) {
                is ApiResult.Success -> {
                    cartStore.clearCart()
                    onSuccess(result.data.orderId ?: 0, result.data.total ?: 0.0)
                }
                is ApiResult.Failure -> errorMessage = result.message
            }
            isSubmitting = false
        }
    }
}

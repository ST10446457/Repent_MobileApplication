package com.repent.app.ui.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.repent.app.data.local.CartStore
import com.repent.app.data.model.CartItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartViewModel(private val cartStore: CartStore) : ViewModel() {

    val cart: StateFlow<List<CartItem>> = cartStore.cartFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList(),
    )

    fun increment(productId: Int, currentQty: Int) {
        viewModelScope.launch { cartStore.updateQuantity(productId, currentQty + 1) }
    }

    fun decrement(productId: Int, currentQty: Int) {
        viewModelScope.launch { cartStore.updateQuantity(productId, currentQty - 1) }
    }

    fun remove(productId: Int) {
        viewModelScope.launch { cartStore.removeItem(productId) }
    }
}

/** Free shipping over R800, flat R95 otherwise — matches api/checkout.php */
fun cartShipping(subtotal: Double): Double = if (subtotal >= 800) 0.0 else 95.0

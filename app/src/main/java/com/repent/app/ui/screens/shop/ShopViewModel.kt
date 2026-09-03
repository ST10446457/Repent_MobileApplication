package com.repent.app.ui.screens.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.repent.app.data.model.Product
import com.repent.app.data.repository.ApiResult
import com.repent.app.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShopViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _category = MutableStateFlow("all")
    val category: StateFlow<String> = _category.asStateFlow()

    fun load(initialCategory: String = "all") {
        _category.value = initialCategory
        fetch()
    }

    fun setCategory(newCategory: String) {
        _category.value = newCategory
        fetch()
    }

    private fun fetch() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            when (val result = repository.getProducts(_category.value)) {
                is ApiResult.Success -> _products.value = result.data
                is ApiResult.Failure -> _errorMessage.value = result.message
            }
            _isLoading.value = false
        }
    }
}
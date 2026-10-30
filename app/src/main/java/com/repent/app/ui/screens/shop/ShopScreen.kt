package com.repent.app.ui.screens.shop

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.repent.app.data.model.Product
import com.repent.app.ui.components.ProductCard
import com.repent.app.ui.components.EmptyState

@Composable
fun ShopScreen(
    initialCategory: String = "all",
    onProductClick: (Int) -> Unit,
    onAddToCart: (Product) -> Unit,
    viewModel: ShopViewModel
) {
    val products by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val selectedCategory by viewModel.category.collectAsState()

    LaunchedEffect(initialCategory) {
        viewModel.load(initialCategory)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(horizontal = 8.dp)) {
            listOf("all" to "All", "tshirts" to "Tees", "hoodies" to "Hoodies", "caps" to "Caps")
                .forEach { (key, title) ->
                    FilterChip(
                        selected = selectedCategory == key,
                        onClick = { viewModel.setCategory(key) },
                        label = { Text(title) },
                    )
                }
        }

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null -> {
                val message = errorMessage
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = message ?: "Unknown error")
                }
            }
            products.isEmpty() -> EmptyState("🛍️", "No products available", "Products will appear here when added to Firestore.")
            else -> LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
            ) {
                items(items = products, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        onClick = { onProductClick(product.id) },
                        onAddToCart = { onAddToCart(product) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

package com.repent.app.ui.screens.shop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.repent.app.data.model.Product

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

    LaunchedEffect(initialCategory) {
        viewModel.load(initialCategory)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row {
            Button(onClick = { viewModel.setCategory("all") }) {
                Text("ALL")
            }
            Button(onClick = { viewModel.setCategory("electronics") }) {
                Text("Electronics")
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
            else -> {
                LazyColumn {
                    items(items = products) { product ->
                        Row {
                            Button(onClick = { onProductClick(product.id) }) {
                                Text(text = product.name)
                            }
                            Button(onClick = { onAddToCart(product) }) {
                                Text("Add to Cart")
                            }
                        }
                    }
                }
            }
        }
    }
}
package com.repent.app.ui.screens.checkout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.repent.app.ui.components.GoldButton
import com.repent.app.ui.rememberRepentApp

@Composable
fun CheckoutScreen(
    onOrderPlaced: (orderId: Int, total: Double) -> Unit,
) {
    val app = rememberRepentApp()
    val viewModel = remember { CheckoutViewModel(app.orderRepository, app.cartStore) }
    val cartItems by app.cartStore.cartFlow.collectAsState(initial = emptyList())

    val subtotal = cartItems.sumOf { it.lineTotal }
    val shipping = if (subtotal >= 800) 0.0 else 95.0
    val total = subtotal + shipping

    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("eft") }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Text("Delivery Details", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        viewModel.errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 12.dp))
        }

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Street Address") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("City / Town") },
                modifier = Modifier.weight(1f),
            )
            OutlinedTextField(
                value = postalCode,
                onValueChange = { postalCode = it },
                label = { Text("Postal Code") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Contact Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(24.dp))
        Text("Payment Method", style = MaterialTheme.typography.titleMedium)
        Column(Modifier.selectableGroup()) {
            listOf("eft" to "EFT / Bank Transfer", "card" to "Credit / Debit Card").forEach { (value, label) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                ) {
                    RadioButton(selected = paymentMethod == value, onClick = { paymentMethod = value })
                    Text(label)
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("Order Summary", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        cartItems.forEach { item ->
            Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${item.name} × ${item.quantity}")
                Text("R${item.lineTotal.toInt()}")
            }
        }
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Shipping")
            Text(if (shipping == 0.0) "FREE" else "R${shipping.toInt()}")
        }
        Row(Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Total", fontWeight = FontWeight.Bold)
            Text("R${total.toInt()}", fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(24.dp))
        GoldButton(
            text = if (viewModel.isSubmitting) "Placing Order…" else "Place Order",
            enabled = !viewModel.isSubmitting && cartItems.isNotEmpty(),
            onClick = {
                viewModel.placeOrder(cartItems, address, city, postalCode, phone, paymentMethod, onOrderPlaced)
            },
        )
        Spacer(Modifier.height(24.dp))
    }
}

package com.repent.app.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HelpScreen() {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)) {
        Text("REPENT† Help", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        HelpEntry("Create an account", "Open Profile, choose Login / Create Account, then select Create Account. Enter your name, email and password.")
        HelpEntry("Sign in", "Open Profile and select Login. Enter the same Firebase account details you used to register.")
        HelpEntry("Shop", "Open Shop, select a category, choose a product and add it to your cart.")
        HelpEntry("Order requests", "Open Cart, review the items and fill in your delivery details. This academic demo records a pending order request; it does not charge a payment card or reserve stock.")
        HelpEntry("Settings and password", "Open Profile and choose Account Settings. Update your name or use Change Password with your current password.")
        HelpEntry("Prayer and devotionals", "Use the Home or Devotionals sections to read content or submit prayer requests. Sign in before submitting.")
        HelpEntry("Troubleshooting", "If data does not load, check the internet connection. Restart the app, then sign in again. Contact your project team if an error persists.")
    }
}

@Composable
private fun HelpEntry(title: String, body: String) {
    Text(title, style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(4.dp))
    Text(body, style = MaterialTheme.typography.bodyMedium)
    Spacer(Modifier.height(20.dp))
}

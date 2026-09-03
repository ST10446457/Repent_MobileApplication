package com.repent.app.ui.screens.newsletter

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.repent.app.data.repository.ApiResult
import com.repent.app.ui.components.GoldButton
import com.repent.app.ui.rememberRepentApp
import com.repent.app.ui.theme.RepentGold
import kotlinx.coroutines.launch

@Composable
fun NewsletterScreen() {
    val app = rememberRepentApp()
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("weekly") }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var resultStatus by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Stay Connected", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(4.dp))
        Text(
            "Subscribe for devotionals, new drops, and encouragement straight to your inbox.",
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(20.dp))

        if (resultStatus != null) {
            Text(
                if (resultStatus == "already") "You're already subscribed — thank you!" else "You're subscribed! Welcome to the family.",
                color = RepentGold,
                style = MaterialTheme.typography.titleMedium,
            )
            return@Column
        }

        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 12.dp))
        }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Your Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))
        Text("How often?", style = MaterialTheme.typography.titleMedium)
        listOf("daily" to "Daily", "weekly" to "Weekly", "monthly" to "Monthly").forEach { (value, label) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = frequency == value, onClick = { frequency = value })
                Text(label)
            }
        }

        Spacer(Modifier.height(20.dp))
        GoldButton(
            text = if (isSubmitting) "Subscribing…" else "Subscribe",
            enabled = !isSubmitting,
            onClick = {
                if (email.isBlank() || name.isBlank()) {
                    errorMessage = "Please enter your name and email address."
                    return@GoldButton
                }
                errorMessage = null
                scope.launch {
                    isSubmitting = true
                    when (val result = app.newsletterRepository.subscribe(name, email, frequency)) {
                        is ApiResult.Success -> resultStatus = result.data
                        is ApiResult.Failure -> errorMessage = result.message
                    }
                    isSubmitting = false
                }
            },
        )
    }
}

package com.repent.app.ui.screens.prayer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
fun PrayerScreen() {
    val app = rememberRepentApp()
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var prayer by remember { mutableStateOf("") }
    var isPrivate by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var submitted by remember { mutableStateOf(false) }

    if (submitted) {
        Column(
            Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text("🙏", style = MaterialTheme.typography.displayLarge)
            Spacer(Modifier.height(16.dp))
            Text("Your prayer request has been received.", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text("Our prayer team will be lifting this up.", color = RepentGold)
            Spacer(Modifier.height(24.dp))
            GoldButton(text = "Submit Another Request", onClick = { submitted = false })
        }
        return
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Text("Prayer Requests", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(4.dp))
        Text("Share what's on your heart. You are not alone.", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(20.dp))

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
            label = { Text("Email (optional)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = subject,
            onValueChange = { subject = it },
            label = { Text("Subject (optional)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = prayer,
            onValueChange = { prayer = it },
            label = { Text("Your Prayer Request") },
            minLines = 5,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isPrivate, onCheckedChange = { isPrivate = it })
            Text("Keep this request private (only visible to the prayer team)")
        }

        Spacer(Modifier.height(20.dp))
        GoldButton(
            text = if (isSubmitting) "Submitting…" else "Submit Prayer Request",
            enabled = !isSubmitting,
            onClick = {
                if (name.isBlank() || prayer.isBlank()) {
                    errorMessage = "Please enter your name and prayer request."
                    return@GoldButton
                }
                errorMessage = null
                scope.launch {
                    isSubmitting = true
                    when (val result = app.prayerRepository.submitPrayer(name, email.ifBlank { null }, subject.ifBlank { null }, prayer, isPrivate)) {
                        is ApiResult.Success -> submitted = true
                        is ApiResult.Failure -> errorMessage = result.message
                    }
                    isSubmitting = false
                }
            },
        )
    }
}

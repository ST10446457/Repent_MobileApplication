package com.repent.app.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.repent.app.data.repository.ApiResult
import com.repent.app.ui.rememberRepentApp
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen() {
    val app = rememberRepentApp()
    val scope = rememberCoroutineScope()
    val user by app.sessionManager.userFlow.collectAsState(initial = null)
    var name by remember(user?.fullname) { mutableStateOf(user?.fullname.orEmpty()) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmation by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)) {
        Text("Account Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))
        Text(user?.email.orEmpty())
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(name, { name = it }, label = { Text("Full name") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Button(enabled = !busy && user != null, onClick = {
            scope.launch {
                busy = true
                message = when (val result = app.authRepository.updateName(name)) {
                    is ApiResult.Success -> "Your name was updated."
                    is ApiResult.Failure -> result.message
                }
                busy = false
            }
        }) { Text("Save profile") }

        Spacer(Modifier.height(32.dp))
        Text("Change Password", style = MaterialTheme.typography.titleLarge)
        Text("For email/password accounts, enter your current password to confirm your identity.")
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(currentPassword, { currentPassword = it }, label = { Text("Current password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(newPassword, { newPassword = it }, label = { Text("New password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(confirmation, { confirmation = it }, label = { Text("Confirm new password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        Button(enabled = !busy && user != null, onClick = {
            if (newPassword != confirmation) {
                message = "New passwords do not match."
            } else {
                scope.launch {
                    busy = true
                    message = when (val result = app.authRepository.changePassword(currentPassword, newPassword)) {
                        is ApiResult.Success -> {
                            currentPassword = ""; newPassword = ""; confirmation = ""
                            "Password changed successfully."
                        }
                        is ApiResult.Failure -> result.message
                    }
                    busy = false
                }
            }
        }) { Text("Change password") }
        Spacer(Modifier.height(12.dp))
        if (busy) CircularProgressIndicator()
        if (message.isNotBlank()) Text(message, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(24.dp))
        Text("Google accounts manage their Google password through their Google Account settings.", style = MaterialTheme.typography.bodySmall)
    }
}

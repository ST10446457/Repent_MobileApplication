package com.repent.app.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.repent.app.data.repository.AuthRepository
import com.repent.app.ui.components.GoldButton
import com.repent.app.ui.rememberRepentApp
import com.repent.app.ui.theme.RepentGold

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
) {
    val app = rememberRepentApp()
    val viewModel = remember { AuthViewModel(app.authRepository) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(32.dp))
        Text("†", style = MaterialTheme.typography.displayLarge, color = RepentGold)
        Spacer(Modifier.height(8.dp))
        Text("Welcome Back", style = MaterialTheme.typography.headlineMedium)
        Text("Sign in to your REPENT† account", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(32.dp))

        viewModel.errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 12.dp))
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(24.dp))

        GoldButton(
            text = if (viewModel.isLoading) "Signing In…" else "Sign In",
            enabled = !viewModel.isLoading,
            onClick = { viewModel.login(email, password, onLoginSuccess) },
        )

        Spacer(Modifier.height(16.dp))
        TextButton(onClick = onNavigateToRegister) {
            Text("New to REPENT†? Create an account")
        }
    }
}

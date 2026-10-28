package com.repent.app.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.repent.app.data.repository.AuthRepository
import com.repent.app.data.repository.ApiResult
import com.repent.app.ui.components.GoldButton
import com.repent.app.ui.rememberRepentApp
import com.repent.app.ui.theme.RepentGold
import kotlinx.coroutines.launch
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
) {
    val app = rememberRepentApp()
    val viewModel = remember { AuthViewModel(app.authRepository) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var resetMessage by remember { mutableStateOf("") }

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
        if (resetMessage.isNotBlank()) Text(resetMessage, modifier = Modifier.padding(bottom = 12.dp))

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

        TextButton(onClick = {
            scope.launch {
                resetMessage = when (val result = app.authRepository.passwordReset(email.trim())) {
                    is ApiResult.Success -> "If the address is registered, password reset instructions have been sent."
                    is ApiResult.Failure -> result.message
                }
            }
        }) { Text("Forgot password?") }

        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            enabled = !viewModel.isLoading,
            onClick = {
                scope.launch {
                    try {
                        val clientIdRes = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
                        require(clientIdRes != 0) { "Google Sign-In needs the app SHA-1 in Firebase and an updated google-services.json." }
                        val option = GetGoogleIdOption.Builder()
                            .setServerClientId(context.getString(clientIdRes))
                            .setFilterByAuthorizedAccounts(false)
                            .build()
                        val request = GetCredentialRequest.Builder().addCredentialOption(option).build()
                        val result = CredentialManager.create(context).getCredential(context, request)
                        val credential = result.credential
                        require(credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                            "A Google account was not selected."
                        }
                        val token = GoogleIdTokenCredential.createFrom(credential.data).idToken
                        resetMessage = when (val login = app.authRepository.loginWithGoogleIdToken(token)) {
                            is ApiResult.Success -> { onLoginSuccess(); "" }
                            is ApiResult.Failure -> login.message
                        }
                    } catch (e: Exception) {
                        resetMessage = e.localizedMessage ?: "Google Sign-In could not complete."
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Continue with Google") }

        Spacer(Modifier.height(16.dp))
        TextButton(onClick = onNavigateToRegister) {
            Text("New to REPENT†? Create an account")
        }
    }
}

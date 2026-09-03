package com.repent.app.ui.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.repent.app.data.repository.ApiResult
import com.repent.app.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        errorMessage = null
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Please enter both your email and password."
            return
        }
        viewModelScope.launch {
            isLoading = true
            when (val result = repository.login(email.trim(), password)) {
                is ApiResult.Success -> onSuccess()
                is ApiResult.Failure -> errorMessage = result.message
            }
            isLoading = false
        }
    }

    fun register(fullname: String, email: String, password: String, confirm: String, onSuccess: () -> Unit) {
        errorMessage = null
        when {
            fullname.isBlank() -> errorMessage = "Full name is required."
            password.length < 6 -> errorMessage = "Password must be at least 6 characters long."
            password != confirm -> errorMessage = "Passwords do not match."
            else -> {
                viewModelScope.launch {
                    isLoading = true
                    when (val result = repository.register(fullname.trim(), email.trim(), password)) {
                        is ApiResult.Success -> onSuccess()
                        is ApiResult.Failure -> errorMessage = result.message
                    }
                    isLoading = false
                }
            }
        }
    }
}

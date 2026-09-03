package com.repent.app.data.repository

import com.repent.app.data.local.SessionManager
import com.repent.app.data.remote.RetrofitClient

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Failure(val message: String) : ApiResult<Nothing>()
}

class AuthRepository(private val sessionManager: SessionManager) {

    private val api = RetrofitClient.apiService

    suspend fun register(fullname: String, email: String, password: String): ApiResult<Unit> {
        return try {
            val response = api.register(mapOf("fullname" to fullname, "email" to email, "password" to password))
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.token != null && body.user != null) {
                sessionManager.saveSession(body.token, body.user)
                ApiResult.Success(Unit)
            } else {
                ApiResult.Failure(body?.error ?: "Registration failed. Please try again.")
            }
        } catch (e: Exception) {
            ApiResult.Failure(e.message ?: "Network error. Check your connection.")
        }
    }

    suspend fun login(email: String, password: String): ApiResult<Unit> {
        return try {
            val response = api.login(mapOf("email" to email, "password" to password))
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.token != null && body.user != null) {
                sessionManager.saveSession(body.token, body.user)
                ApiResult.Success(Unit)
            } else {
                ApiResult.Failure(body?.error ?: "Incorrect email or password.")
            }
        } catch (e: Exception) {
            ApiResult.Failure(e.message ?: "Network error. Check your connection.")
        }
    }

    suspend fun logout() {
        val token = sessionManager.currentToken()
        if (token != null) {
            runCatching { api.logout(RetrofitClient.bearer(token)) }
        }
        sessionManager.clearSession()
    }
}

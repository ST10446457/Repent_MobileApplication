package com.repent.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.repent.app.data.firebase.FirebaseAccountService
import com.repent.app.data.local.SessionManager
import com.repent.app.data.model.User
import kotlinx.coroutines.tasks.await

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Failure(val message: String) : ApiResult<Nothing>()
}

class AuthRepository(private val sessionManager: SessionManager) {
    private val service = FirebaseAccountService()
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun register(fullname: String, email: String, password: String): ApiResult<Unit> = runSafe {
        service.register(fullname, email, password)
        saveCurrentUser()
    }

    suspend fun login(email: String, password: String): ApiResult<Unit> = runSafe {
        service.login(email, password)
        saveCurrentUser()
    }

    suspend fun loginWithGoogleIdToken(idToken: String): ApiResult<Unit> = runSafe {
        val account = auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await()
        val user = requireNotNull(account.user)
        val profileRef = firestore.collection("users").document(user.uid)
        if (!profileRef.get().await().exists()) {
            profileRef.set(mapOf(
                "uid" to user.uid,
                "fullname" to (user.displayName ?: "REPENT customer"),
                "email" to user.email.orEmpty(),
                "role" to "customer",
                "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
            )).await()
        }
        saveCurrentUser()
    }

    suspend fun passwordReset(email: String): ApiResult<Unit> = runSafe {
        service.sendPasswordReset(email)
    }

    suspend fun changePassword(current: String, replacement: String): ApiResult<Unit> = runSafe {
        service.changePassword(current, replacement)
    }

    suspend fun updateName(name: String): ApiResult<Unit> = runSafe {
        service.updateDisplayName(name)
        saveCurrentUser()
    }

    suspend fun syncCurrentUser(): ApiResult<Unit> = runSafe { saveCurrentUser() }

    private suspend fun saveCurrentUser() {
        val firebaseUser = requireNotNull(auth.currentUser) { "Please sign in." }
        val profile = firestore.collection("users").document(firebaseUser.uid).get().await()
        val role = if (firebaseUser.getIdToken(false).await().claims["admin"] == true) "admin" else "customer"
        sessionManager.saveSession(
            firebaseUser.uid,
            User(
                id = 0,
                fullname = profile.getString("fullname") ?: firebaseUser.displayName ?: "REPENT customer",
                email = firebaseUser.email.orEmpty(),
                role = role,
            ),
        )
    }

    suspend fun logout() {
        service.logout()
        sessionManager.clearSession()
    }

    private suspend inline fun <T> runSafe(crossinline action: suspend () -> T): ApiResult<T> = try {
        ApiResult.Success(action())
    } catch (e: Exception) {
        ApiResult.Failure(e.localizedMessage ?: "Request failed. Please try again.")
    }
}

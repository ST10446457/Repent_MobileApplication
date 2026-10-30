package com.repent.app.data.firebase

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Firebase account operations used by both the login and Settings migration.
 * Passwords are sent over the Firebase SDK's encrypted connection and are never
 * written to Firestore or to the app's preference storage.
 */
class FirebaseAccountService(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
    suspend fun register(name: String, email: String, password: String) {
        require(name.isNotBlank()) { "Please enter your full name." }
        require(AccountValidation.validEmail(email)) { "Please enter a valid email address." }
        require(password.length >= 6) { "Your password must be at least 6 characters." }

        val account = auth.createUserWithEmailAndPassword(email.trim(), password).await()
        val user = requireNotNull(account.user)
        db.collection("users").document(user.uid).set(
            mapOf(
                "uid" to user.uid,
                "fullname" to name.trim(),
                "email" to (user.email ?: email.trim()),
                "role" to "customer",
                "createdAt" to FieldValue.serverTimestamp(),
            ),
        ).await()
        user.sendEmailVerification().await()
        auth.signOut()
    }

    suspend fun login(email: String, password: String) {
        require(email.isNotBlank() && password.isNotBlank()) { "Enter your email and password." }
        val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
        if (result.user?.isEmailVerified != true) {
            result.user?.sendEmailVerification()?.await()
            auth.signOut()
            throw IllegalStateException("Please verify your email before signing in. Check your inbox.")
        }
    }

    suspend fun sendPasswordReset(email: String) {
        require(AccountValidation.validEmail(email)) { "Please enter a valid email address." }
        auth.sendPasswordResetEmail(email.trim()).await()
    }

    suspend fun changePassword(currentPassword: String, newPassword: String) {
        val user = requireNotNull(auth.currentUser) { "Please sign in again." }
        val email = requireNotNull(user.email) { "Use your Google account to manage your password." }
        require(newPassword.length >= 6) { "Your new password must contain at least 6 characters." }
        user.reauthenticate(EmailAuthProvider.getCredential(email, currentPassword)).await()
        user.updatePassword(newPassword).await()
    }

    suspend fun updateDisplayName(name: String) {
        val user = requireNotNull(auth.currentUser) { "Please sign in again." }
        require(name.isNotBlank()) { "Please enter your name." }
        db.collection("users").document(user.uid).update("fullname", name.trim()).await()
    }

    fun logout() = auth.signOut()
}

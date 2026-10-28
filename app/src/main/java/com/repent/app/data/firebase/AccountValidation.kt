package com.repent.app.data.firebase

object AccountValidation {
    fun validEmail(value: String): Boolean {
        val email = value.trim()
        return email.length <= 254 && email.contains('@') &&
            email.substringAfter('@').contains('.') && !email.contains(' ')
    }

    fun registrationError(name: String, email: String, password: String, confirm: String): String? = when {
        name.isBlank() -> "Full name is required."
        !validEmail(email) -> "Please enter a valid email address."
        password.length < 6 -> "Password must be at least 6 characters long."
        password != confirm -> "Passwords do not match."
        else -> null
    }

    fun deliveryError(address: String, city: String, postcode: String, phone: String): String? =
        if (listOf(address, city, postcode, phone).any { it.isBlank() })
            "Please fill in all delivery details." else null
}

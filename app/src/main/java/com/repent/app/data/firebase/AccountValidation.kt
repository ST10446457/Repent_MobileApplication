package com.repent.app.data.firebase

object AccountValidation {
    // Format validation only. Email ownership is confirmed through Firebase's verification email.
    private val local = Regex("^[A-Za-z0-9](?:[A-Za-z0-9.!#$%&'*+/=?^`{|}~-]{0,62}[A-Za-z0-9])?$")
    private val domainLabel = Regex("^[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?$")

    fun validEmail(value: String): Boolean {
        val email = value.trim()
        if (email.length !in 6..254 || email.any { it.isWhitespace() }) return false
        if (email.count { it == '@' } != 1) return false
        val (mailbox, domain) = email.split('@')
        if (mailbox.length > 64 || !local.matches(mailbox) ||
            mailbox.contains("..") || mailbox.startsWith('.') || mailbox.endsWith('.')) return false
        val labels = domain.split('.')
        if (labels.size < 2 || labels.any { !domainLabel.matches(it) }) return false
        val tld = labels.last()
        if (tld.length < 2 || !tld.all { it.isLetter() }) return false
        return true
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

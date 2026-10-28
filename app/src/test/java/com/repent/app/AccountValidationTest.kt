package com.repent.app

import com.repent.app.data.firebase.AccountValidation
import org.junit.Assert.*
import org.junit.Test

class AccountValidationTest {
    @Test fun rejectsBlankName() {
        assertEquals("Full name is required.", AccountValidation.registrationError("", "a@b.co", "123456", "123456"))
    }

    @Test fun rejectsMalformedEmail() {
        assertFalse(AccountValidation.validEmail("not-an-email"))
        assertFalse(AccountValidation.validEmail("a@b"))
    }

    @Test fun rejectsShortPassword() {
        assertEquals("Password must be at least 6 characters long.", AccountValidation.registrationError("Alex", "a@b.co", "123", "123"))
    }

    @Test fun rejectsPasswordMismatch() {
        assertEquals("Passwords do not match.", AccountValidation.registrationError("Alex", "a@b.co", "123456", "654321"))
    }

    @Test fun acceptsValidRegistration() {
        assertNull(AccountValidation.registrationError("Alex", "alex@example.com", "abcdef12", "abcdef12"))
    }

    @Test fun requiresDeliveryDetails() {
        assertNotNull(AccountValidation.deliveryError("", "Pretoria", "0001", "0123456789"))
        assertNull(AccountValidation.deliveryError("123 Road", "Pretoria", "0001", "0123456789"))
    }
}

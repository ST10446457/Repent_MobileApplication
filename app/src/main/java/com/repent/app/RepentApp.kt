package com.repent.app

import android.app.Application
import com.repent.app.data.local.CartStore
import com.repent.app.data.local.SessionManager
import com.repent.app.data.repository.AdminRepository
import com.repent.app.data.repository.AuthRepository
import com.repent.app.data.repository.DevotionalRepository
import com.repent.app.data.repository.NewsletterRepository
import com.repent.app.data.repository.OrderRepository
import com.repent.app.data.repository.PrayerRepository
import com.repent.app.data.repository.ProductRepository

/**
 * Simple hand-rolled service locator. A student/demo project doesn't
 * need Hilt/Dagger — this keeps one instance of each repository alive
 * for the whole app and hands them out to ViewModels via AppViewModelFactory.
 */
class RepentApp : Application() {

    lateinit var sessionManager: SessionManager
        private set
    lateinit var cartStore: CartStore
        private set

    lateinit var authRepository: AuthRepository
        private set
    lateinit var productRepository: ProductRepository
        private set
    lateinit var devotionalRepository: DevotionalRepository
        private set
    lateinit var prayerRepository: PrayerRepository
        private set
    lateinit var newsletterRepository: NewsletterRepository
        private set
    lateinit var orderRepository: OrderRepository
        private set
    lateinit var adminRepository: AdminRepository
        private set

    override fun onCreate() {
        super.onCreate()
        sessionManager = SessionManager(this)
        cartStore = CartStore(this)

        authRepository = AuthRepository(sessionManager)
        productRepository = ProductRepository()
        devotionalRepository = DevotionalRepository()
        prayerRepository = PrayerRepository()
        newsletterRepository = NewsletterRepository()
        orderRepository = OrderRepository(sessionManager)
        adminRepository = AdminRepository(sessionManager)
    }
}

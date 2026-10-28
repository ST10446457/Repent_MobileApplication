package com.repent.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.repent.app.data.local.SessionManager
import com.repent.app.data.model.*
import kotlinx.coroutines.tasks.await

/** All dashboard reads are enforced by Firestore's admin custom-claim rule. */
class AdminRepository(@Suppress("UNUSED_PARAMETER") sessionManager: SessionManager) {
    private suspend fun requireAdmin() {
        val claims = FirebaseAuth.getInstance().currentUser?.getIdToken(false)?.await()?.claims
        require(claims?.get("admin") == true) { "Administrator permissions are required." }
    }

    suspend fun getStats(): ApiResult<AdminStats> = safe {
        requireAdmin()
        AdminStats(
            orders = firestore.collection("orders").get().await().size(),
            users = firestore.collection("users").get().await().size(),
            products = firestore.collection("products").get().await().size(),
            prayers = firestore.collection("prayer_requests").get().await().size(),
        )
    }

    suspend fun getOrders(): ApiResult<List<AdminOrder>> = safe {
        requireAdmin()
        firestore.collection("orders").get().await().documents.map { doc ->
            AdminOrder(
                id = doc.getLong("orderNumber")?.toInt() ?: 0,
                customer = doc.getString("userId") ?: "Unknown",
                total = doc.getDouble("total") ?: 0.0,
                status = doc.getString("status") ?: "pending",
                createdAt = doc.getTimestamp("createdAt")?.toDate()?.toString(),
            )
        }
    }

    suspend fun updateOrderStatus(orderId: Int, status: String): ApiResult<Unit> = safe {
        requireAdmin()
        require(status in setOf("pending", "processing", "shipped", "delivered", "cancelled")) { "Invalid order status." }
        val matching = firestore.collection("orders").whereEqualTo("orderNumber", orderId).get().await().documents
        require(matching.size == 1) { "Order not found or order number is ambiguous." }
        matching.first().reference.update("status", status).await()
    }

    suspend fun getPrayers(): ApiResult<List<AdminPrayer>> = safe {
        requireAdmin()
        firestore.collection("prayer_requests").get().await().documents.mapIndexed { index, doc ->
            AdminPrayer(
                id = index + 1, name = doc.getString("name") ?: "Anonymous",
                email = doc.getString("email"), subject = doc.getString("subject"),
                prayer = doc.getString("prayer").orEmpty(), isPrivate = doc.getBoolean("isPrivate") ?: false,
                submittedAt = doc.getTimestamp("submittedAt")?.toDate()?.toString(),
            )
        }
    }

    suspend fun getSubscribers(): ApiResult<List<AdminSubscriber>> = safe {
        requireAdmin()
        firestore.collection("newsletter_subscribers").get().await().documents.mapIndexed { index, doc ->
            AdminSubscriber(
                id = index + 1, name = doc.getString("name").orEmpty(),
                email = doc.getString("email").orEmpty(), frequency = doc.getString("frequency") ?: "weekly",
                subscribedAt = doc.getTimestamp("subscribedAt")?.toDate()?.toString(),
            )
        }
    }

    suspend fun removeSubscriber(id: Int): ApiResult<Unit> = ApiResult.Failure(
        "Delete subscribers from the authenticated Firebase Admin Console to avoid accidental removal.",
    )

    suspend fun getProducts(): ApiResult<List<Product>> {
        return when (val result = ProductRepository().getProducts()) {
            is ApiResult.Success -> result
            is ApiResult.Failure -> result
        }
    }

    suspend fun deleteProduct(id: Int): ApiResult<Unit> = ApiResult.Failure(
        "Delete products using the authorized catalogue management workflow.",
    )

    private suspend inline fun <T> safe(crossinline action: suspend () -> T): ApiResult<T> = try {
        ApiResult.Success(action())
    } catch (e: Exception) {
        ApiResult.Failure(e.localizedMessage ?: "Unable to load administrator data.")
    }
}

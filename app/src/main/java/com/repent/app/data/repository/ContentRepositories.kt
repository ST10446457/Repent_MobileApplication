package com.repent.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import com.repent.app.data.model.Devotional
import com.repent.app.data.model.Product
import kotlinx.coroutines.tasks.await

internal val firestore: FirebaseFirestore get() = FirebaseFirestore.getInstance()

internal fun DocumentSnapshot.asProduct() = Product(
    id = getLong("id")?.toInt() ?: id.toIntOrNull() ?: 0,
    name = getString("name").orEmpty(),
    description = getString("description"),
    price = getDouble("price") ?: getLong("price")?.toDouble() ?: 0.0,
    category = getString("category").orEmpty(),
    image = getString("image"),
    stock = getLong("stock")?.toInt() ?: 0,
    badge = getString("badge"),
)

class ProductRepository {
    suspend fun getProducts(category: String = "all"): ApiResult<List<Product>> = try {
        val result = firestore.collection("products").get().await().documents
            .map { it.asProduct() }
            .filter { category == "all" || it.category == category }
            .sortedBy { it.id }
        ApiResult.Success(result)
    } catch (e: Exception) {
        ApiResult.Failure(e.localizedMessage ?: "Unable to load products.")
    }

    suspend fun getProduct(id: Int): ApiResult<Product> = when (val result = getProducts()) {
        is ApiResult.Failure -> result
        is ApiResult.Success -> result.data.firstOrNull { it.id == id }
            ?.let { ApiResult.Success(it) } ?: ApiResult.Failure("Product not found.")
    }
}

class DevotionalRepository {
    suspend fun getDevotionals(): ApiResult<List<Devotional>> = try {
        val result = firestore.collection("devotionals").get().await().documents.map { doc ->
            Devotional(
                id = doc.getLong("id")?.toInt() ?: 0,
                title = doc.getString("title").orEmpty(),
                verse = doc.getString("verse").orEmpty(),
                reference = doc.getString("reference"),
                content = doc.getString("content").orEmpty(),
                topic = doc.getString("topic"),
                createdAt = doc.getTimestamp("createdAt")?.toDate()?.toString(),
            )
        }
        ApiResult.Success(result)
    } catch (e: Exception) {
        ApiResult.Failure(e.localizedMessage ?: "Unable to load devotionals.")
    }
}

class PrayerRepository {
    suspend fun submitPrayer(name: String, email: String?, subject: String?, prayer: String, isPrivate: Boolean): ApiResult<Unit> = try {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalStateException("Please sign in to submit a prayer request.")
        firestore.collection("prayer_requests").add(
            mapOf(
                "userId" to uid, "name" to name.trim(), "email" to (email ?: ""),
                "subject" to (subject ?: ""), "prayer" to prayer.trim(),
                "isPrivate" to isPrivate, "submittedAt" to FieldValue.serverTimestamp(),
            ),
        ).await()
        ApiResult.Success(Unit)
    } catch (e: Exception) {
        ApiResult.Failure(e.localizedMessage ?: "Unable to send prayer request.")
    }
}

class NewsletterRepository {
    suspend fun subscribe(name: String, email: String, frequency: String): ApiResult<String> = try {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalStateException("Please sign in to subscribe.")
        firestore.collection("newsletter_subscribers").document(uid).set(
            mapOf(
                "userId" to uid, "name" to name.trim(), "email" to email.trim(),
                "frequency" to frequency, "subscribedAt" to FieldValue.serverTimestamp(),
            ),
        ).await()
        ApiResult.Success("subscribed")
    } catch (e: Exception) {
        ApiResult.Failure(e.localizedMessage ?: "Unable to subscribe.")
    }
}

package com.repent.app.data.repository

import com.repent.app.data.model.Devotional
import com.repent.app.data.model.NewsletterRequestBody
import com.repent.app.data.model.PrayerRequestBody
import com.repent.app.data.model.Product
import com.repent.app.data.remote.RetrofitClient

class ProductRepository {
    private val api = RetrofitClient.apiService

    suspend fun getProducts(category: String = "all"): ApiResult<List<Product>> {
        return try {
            val response = api.getProducts(category)
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                ApiResult.Success(body.products)
            } else {
                ApiResult.Failure(body?.error ?: "Could not load products.")
            }
        } catch (e: Exception) {
            ApiResult.Failure(e.message ?: "Network error. Check your connection.")
        }
    }

    suspend fun getProduct(id: Int): ApiResult<Product> {
        return try {
            val response = api.getProduct(id)
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.product != null) {
                ApiResult.Success(body.product)
            } else {
                ApiResult.Failure(body?.error ?: "Product not found.")
            }
        } catch (e: Exception) {
            ApiResult.Failure(e.message ?: "Network error. Check your connection.")
        }
    }
}

class DevotionalRepository {
    private val api = RetrofitClient.apiService

    suspend fun getDevotionals(): ApiResult<List<Devotional>> {
        return try {
            val response = api.getDevotionals()
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                ApiResult.Success(body.devotionals)
            } else {
                ApiResult.Failure(body?.error ?: "Could not load devotionals.")
            }
        } catch (e: Exception) {
            ApiResult.Failure(e.message ?: "Network error. Check your connection.")
        }
    }
}

class PrayerRepository {
    private val api = RetrofitClient.apiService

    suspend fun submitPrayer(
        name: String,
        email: String?,
        subject: String?,
        prayer: String,
        isPrivate: Boolean,
    ): ApiResult<Unit> {
        return try {
            val response = api.submitPrayer(PrayerRequestBody(name, email, subject, prayer, isPrivate))
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                ApiResult.Success(Unit)
            } else {
                ApiResult.Failure(body?.error ?: "Could not submit your prayer request.")
            }
        } catch (e: Exception) {
            ApiResult.Failure(e.message ?: "Network error. Check your connection.")
        }
    }
}

class NewsletterRepository {
    private val api = RetrofitClient.apiService

    /** Returns "subscribed" or "already" on success. */
    suspend fun subscribe(name: String, email: String, frequency: String): ApiResult<String> {
        return try {
            val response = api.subscribeNewsletter(NewsletterRequestBody(name, email, frequency))
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                ApiResult.Success(body.status ?: "subscribed")
            } else {
                ApiResult.Failure(body?.error ?: "Subscription failed. Please try again.")
            }
        } catch (e: Exception) {
            ApiResult.Failure(e.message ?: "Network error. Check your connection.")
        }
    }
}

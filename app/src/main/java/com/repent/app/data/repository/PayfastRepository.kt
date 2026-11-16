package com.repent.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.repent.app.BuildConfig
import com.repent.app.data.model.CheckoutItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Shared Firebase-backed PayFast sandbox integration.
 * Never store merchant IDs, passphrases, signatures, or card data in the Android app.
 * This only creates a pending order and opens the hosted checkout, not a paid order.
 */
class PayfastRepository {
    suspend fun createCheckout(
        items: List<CheckoutItem>, address: String, city: String, postalCode: String, phone: String
    ): ApiResult<String> = withContext(Dispatchers.IO) {
        try {
            val current = FirebaseAuth.getInstance().currentUser
                ?: throw IllegalStateException("Sign in before checkout.")
            val jwt = current.getIdToken(true).await().token
                ?: throw IllegalStateException("Firebase authentication unavailable.")
            val payload = JSONObject().apply {
                put("items", JSONArray().apply {
                    items.forEach { item ->
                        put(JSONObject().apply {
                            put("productId", item.productId)
                            put("quantity", item.quantity)
                        })
                    }
                })
                put("address", address)
                put("city", city)
                put("postalCode", postalCode)
                put("phone", phone)
            }
            val base = BuildConfig.PAYFAST_API_BASE_URL.trimEnd('/')
            require(base.startsWith("https://")) { "Secure checkout URL required." }
            val conn = (URL("$base/create").openConnection() as HttpURLConnection)
            try {
                conn.requestMethod = "POST"
                conn.connectTimeout = 15000
                conn.readTimeout = 15000
                conn.doOutput = true
                conn.setRequestProperty("Authorization", "Bearer $jwt")
                conn.setRequestProperty("Content-Type", "application/json")
                conn.outputStream.use { it.write(payload.toString().toByteArray(Charsets.UTF_8)) }
                val stream = if (conn.responseCode in 200..299) conn.inputStream else conn.errorStream
                val body = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
                if (conn.responseCode !in 200..299) {
                    throw IllegalStateException(
                        runCatching { JSONObject(body).optString("error") }.getOrNull()
                            ?.ifBlank { "Unable to start payment." } ?: "Unable to start payment."
                    )
                }
                val url = JSONObject(body).getString("checkoutUrl")
                require(url.startsWith("$base/pay/")) { "Invalid checkout destination." }
                ApiResult.Success(url)
            } finally {
                conn.disconnect()
            }
        } catch (e: Exception) {
            ApiResult.Failure(e.localizedMessage ?: "Unable to start PayFast checkout.")
        }
    }
}

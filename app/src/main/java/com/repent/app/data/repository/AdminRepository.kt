package com.repent.app.data.repository

import com.repent.app.data.local.SessionManager
import com.repent.app.data.model.*
import com.repent.app.data.remote.RetrofitClient

class AdminRepository(private val sessionManager: SessionManager) {
    private val api = RetrofitClient.apiService

    private suspend fun authHeader(): String? = sessionManager.currentToken()?.let { RetrofitClient.bearer(it) }

    suspend fun getStats(): ApiResult<AdminStats> {
        val auth = authHeader() ?: return ApiResult.Failure("Not authenticated.")
        return try {
            val response = api.getAdminStats(auth)
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.stats != null) {
                ApiResult.Success(body.stats)
            } else ApiResult.Failure(body?.error ?: "Could not load dashboard stats.")
        } catch (e: Exception) {
            ApiResult.Failure(e.message ?: "Network error.")
        }
    }

    suspend fun getOrders(): ApiResult<List<AdminOrder>> {
        val auth = authHeader() ?: return ApiResult.Failure("Not authenticated.")
        return try {
            val response = api.getAdminOrders(auth)
            val body = response.body()
            if (response.isSuccessful && body?.success == true) ApiResult.Success(body.orders)
            else ApiResult.Failure(body?.error ?: "Could not load orders.")
        } catch (e: Exception) {
            ApiResult.Failure(e.message ?: "Network error.")
        }
    }

    suspend fun updateOrderStatus(orderId: Int, status: String): ApiResult<Unit> {
        val auth = authHeader() ?: return ApiResult.Failure("Not authenticated.")
        return try {
            val response = api.updateOrderStatus(auth, mapOf("order_id" to orderId.toString(), "status" to status))
            val body = response.body()
            if (response.isSuccessful && body?.success == true) ApiResult.Success(Unit)
            else ApiResult.Failure(body?.error ?: "Could not update order.")
        } catch (e: Exception) {
            ApiResult.Failure(e.message ?: "Network error.")
        }
    }

    suspend fun getPrayers(): ApiResult<List<AdminPrayer>> {
        val auth = authHeader() ?: return ApiResult.Failure("Not authenticated.")
        return try {
            val response = api.getAdminPrayers(auth)
            val body = response.body()
            if (response.isSuccessful && body?.success == true) ApiResult.Success(body.prayers)
            else ApiResult.Failure(body?.error ?: "Could not load prayer requests.")
        } catch (e: Exception) {
            ApiResult.Failure(e.message ?: "Network error.")
        }
    }

    suspend fun getSubscribers(): ApiResult<List<AdminSubscriber>> {
        val auth = authHeader() ?: return ApiResult.Failure("Not authenticated.")
        return try {
            val response = api.getAdminSubscribers(auth)
            val body = response.body()
            if (response.isSuccessful && body?.success == true) ApiResult.Success(body.subscribers)
            else ApiResult.Failure(body?.error ?: "Could not load subscribers.")
        } catch (e: Exception) {
            ApiResult.Failure(e.message ?: "Network error.")
        }
    }

    suspend fun removeSubscriber(id: Int): ApiResult<Unit> {
        val auth = authHeader() ?: return ApiResult.Failure("Not authenticated.")
        return try {
            val response = api.deleteSubscriber(auth, mapOf("id" to id))
            val body = response.body()
            if (response.isSuccessful && body?.success == true) ApiResult.Success(Unit)
            else ApiResult.Failure(body?.error ?: "Could not remove subscriber.")
        } catch (e: Exception) {
            ApiResult.Failure(e.message ?: "Network error.")
        }
    }

    suspend fun getProducts(): ApiResult<List<Product>> {
        val auth = authHeader() ?: return ApiResult.Failure("Not authenticated.")
        return try {
            val response = api.getAdminProducts(auth)
            val body = response.body()
            if (response.isSuccessful && body?.success == true) ApiResult.Success(body.products)
            else ApiResult.Failure(body?.error ?: "Could not load products.")
        } catch (e: Exception) {
            ApiResult.Failure(e.message ?: "Network error.")
        }
    }

    suspend fun deleteProduct(id: Int): ApiResult<Unit> {
        val auth = authHeader() ?: return ApiResult.Failure("Not authenticated.")
        return try {
            val response = api.deleteProduct(auth, mapOf("id" to id))
            val body = response.body()
            if (response.isSuccessful && body?.success == true) ApiResult.Success(Unit)
            else ApiResult.Failure(body?.error ?: "Could not delete product.")
        } catch (e: Exception) {
            ApiResult.Failure(e.message ?: "Network error.")
        }
    }
}

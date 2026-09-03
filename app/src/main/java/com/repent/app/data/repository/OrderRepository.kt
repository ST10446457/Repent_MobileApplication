package com.repent.app.data.repository

import com.repent.app.data.local.SessionManager
import com.repent.app.data.model.CheckoutItem
import com.repent.app.data.model.CheckoutRequest
import com.repent.app.data.model.CheckoutResponse
import com.repent.app.data.model.Order
import com.repent.app.data.remote.RetrofitClient

class OrderRepository(private val sessionManager: SessionManager) {
    private val api = RetrofitClient.apiService

    suspend fun checkout(
        items: List<CheckoutItem>,
        shippingAddress: String,
        city: String,
        postalCode: String,
        phone: String,
        paymentMethod: String,
    ): ApiResult<CheckoutResponse> {
        val token = sessionManager.currentToken()
            ?: return ApiResult.Failure("You need to be logged in to check out.")

        return try {
            val response = api.checkout(
                RetrofitClient.bearer(token),
                CheckoutRequest(items, shippingAddress, city, postalCode, phone, paymentMethod),
            )
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                ApiResult.Success(body)
            } else {
                ApiResult.Failure(body?.error ?: "Could not process your order.")
            }
        } catch (e: Exception) {
            ApiResult.Failure(e.message ?: "Network error. Check your connection.")
        }
    }

    suspend fun getMyOrders(): ApiResult<List<Order>> {
        val token = sessionManager.currentToken()
            ?: return ApiResult.Failure("You need to be logged in to view orders.")

        return try {
            val response = api.getMyOrders(RetrofitClient.bearer(token))
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                ApiResult.Success(body.orders)
            } else {
                ApiResult.Failure(body?.error ?: "Could not load your orders.")
            }
        } catch (e: Exception) {
            ApiResult.Failure(e.message ?: "Network error. Check your connection.")
        }
    }
}

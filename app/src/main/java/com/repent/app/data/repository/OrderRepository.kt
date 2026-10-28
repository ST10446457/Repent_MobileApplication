package com.repent.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.repent.app.data.local.SessionManager
import com.repent.app.data.model.CheckoutItem
import com.repent.app.data.model.CheckoutResponse
import com.repent.app.data.model.Order
import com.repent.app.data.model.OrderItem
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

/** Demo order requests. No real charge or inventory reservation occurs here. */
class OrderRepository(@Suppress("UNUSED_PARAMETER") sessionManager: SessionManager) {

    suspend fun checkout(
        items: List<CheckoutItem>, shippingAddress: String, city: String,
        postalCode: String, phone: String, paymentMethod: String,
    ): ApiResult<CheckoutResponse> = try {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalStateException("Please log in before checkout.")
        require(items.isNotEmpty()) { "Your cart is empty." }
        require(shippingAddress.isNotBlank() && city.isNotBlank() && postalCode.isNotBlank() && phone.isNotBlank()) {
            "Please fill in all delivery information."
        }
        val products = when (val result = ProductRepository().getProducts()) {
            is ApiResult.Success -> result.data.associateBy { it.id }
            is ApiResult.Failure -> throw IllegalStateException(result.message)
        }
        var subtotal = 0.0
        val itemData = items.map { item ->
            val product = products[item.productId] ?: throw IllegalArgumentException("A product no longer exists.")
            require(item.quantity > 0 && item.quantity <= product.stock) { "Insufficient stock for ${product.name}." }
            subtotal += product.price * item.quantity
            mapOf(
                "productId" to product.id, "productName" to product.name,
                "price" to product.price, "quantity" to item.quantity,
            )
        }
        val shipping = if (subtotal >= 800.0) 0.0 else 95.0
        val total = subtotal + shipping
        val orderId = Random.nextInt(100_000_000, 999_999_999)
        firestore.collection("orders").add(
            mapOf(
                "userId" to uid,
                "orderNumber" to orderId,
                "items" to itemData,
                "subtotal" to subtotal,
                "shipping" to shipping,
                "total" to total,
                "shippingAddress" to shippingAddress.trim(),
                "city" to city.trim(), "postalCode" to postalCode.trim(),
                "phone" to phone.trim(), "paymentMethod" to paymentMethod,
                "status" to "pending",
                "createdAt" to FieldValue.serverTimestamp(),
            ),
        ).await()
        ApiResult.Success(CheckoutResponse(true, orderId, subtotal, shipping, total, null))
    } catch (e: Exception) {
        ApiResult.Failure(e.localizedMessage ?: "Order request could not be recorded.")
    }

    suspend fun getMyOrders(): ApiResult<List<Order>> = try {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalStateException("Please log in to view orders.")
        val result = firestore.collection("orders").whereEqualTo("userId", uid).get().await()
        val orders = result.documents.map { doc ->
            @Suppress("UNCHECKED_CAST")
            val raw = doc.get("items") as? List<Map<String, Any?>> ?: emptyList()
            Order(
                id = doc.getLong("orderNumber")?.toInt() ?: 0,
                total = doc.getDouble("total") ?: 0.0,
                shipping = doc.getDouble("shipping") ?: 0.0,
                shippingAddress = doc.getString("shippingAddress"),
                city = doc.getString("city"), postalCode = doc.getString("postalCode"),
                phone = doc.getString("phone"), paymentMethod = doc.getString("paymentMethod"),
                status = doc.getString("status") ?: "pending",
                createdAt = doc.getTimestamp("createdAt")?.toDate()?.toString(),
                items = raw.map { item ->
                    OrderItem(
                        productId = (item["productId"] as? Number)?.toInt() ?: 0,
                        productName = item["productName"] as? String ?: "",
                        price = (item["price"] as? Number)?.toDouble() ?: 0.0,
                        quantity = (item["quantity"] as? Number)?.toInt() ?: 0,
                    )
                },
            )
        }.sortedByDescending { it.createdAt }
        ApiResult.Success(orders)
    } catch (e: Exception) {
        ApiResult.Failure(e.localizedMessage ?: "Unable to retrieve your orders.")
    }
}

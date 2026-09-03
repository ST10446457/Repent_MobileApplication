package com.repent.app.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val fullname: String,
    val email: String,
    val role: String,
) {
    val isAdmin: Boolean get() = role == "admin"
}

data class AuthResponse(
    val success: Boolean,
    val token: String?,
    val user: User?,
    val error: String?,
)

data class Product(
    val id: Int,
    val name: String,
    val description: String?,
    val price: Double,
    val category: String,
    val image: String?,
    val stock: Int,
    val badge: String?,
)

data class ProductsResponse(
    val success: Boolean,
    val products: List<Product> = emptyList(),
    val error: String?,
)

data class ProductResponse(
    val success: Boolean,
    val product: Product?,
    val error: String?,
)

data class Devotional(
    val id: Int,
    val title: String,
    val verse: String,
    val reference: String?,
    val content: String,
    val topic: String?,
    @SerializedName("created_at") val createdAt: String?,
)

data class DevotionalsResponse(
    val success: Boolean,
    val devotionals: List<Devotional> = emptyList(),
    val error: String?,
)

data class SimpleResponse(
    val success: Boolean,
    val status: String? = null,
    val error: String? = null,
)

// ----- Cart (client-side only — mirrors what pages/cart.php stores in the PHP session) -----
data class CartItem(
    val productId: Int,
    val name: String,
    val price: Double,
    val image: String?,
    val quantity: Int,
) {
    val lineTotal: Double get() = price * quantity
}

// ----- Checkout -----
data class CheckoutItem(
    @SerializedName("product_id") val productId: Int,
    val quantity: Int,
)

data class CheckoutRequest(
    val items: List<CheckoutItem>,
    @SerializedName("shipping_address") val shippingAddress: String,
    val city: String,
    @SerializedName("postal_code") val postalCode: String,
    val phone: String,
    @SerializedName("payment_method") val paymentMethod: String,
)

data class CheckoutResponse(
    val success: Boolean,
    @SerializedName("order_id") val orderId: Int?,
    val subtotal: Double?,
    val shipping: Double?,
    val total: Double?,
    val error: String?,
)

data class OrderItem(
    @SerializedName("product_id") val productId: Int,
    @SerializedName("product_name") val productName: String,
    val price: Double,
    val quantity: Int,
)

data class Order(
    val id: Int,
    val total: Double,
    val shipping: Double,
    @SerializedName("shipping_address") val shippingAddress: String?,
    val city: String?,
    @SerializedName("postal_code") val postalCode: String?,
    val phone: String?,
    @SerializedName("payment_method") val paymentMethod: String?,
    val status: String,
    @SerializedName("created_at") val createdAt: String?,
    val items: List<OrderItem> = emptyList(),
)

data class OrdersResponse(
    val success: Boolean,
    val orders: List<Order> = emptyList(),
    val error: String?,
)

// ----- Prayer & Newsletter -----
data class PrayerRequestBody(
    val name: String,
    val email: String?,
    val subject: String?,
    val prayer: String,
    @SerializedName("is_private") val isPrivate: Boolean,
)

data class NewsletterRequestBody(
    val name: String,
    val email: String,
    val frequency: String,
)

// ----- Admin -----
data class AdminStats(
    val orders: Int,
    val users: Int,
    val products: Int,
    val prayers: Int,
)

data class AdminStatsResponse(
    val success: Boolean,
    val stats: AdminStats?,
    val error: String?,
)

data class AdminOrder(
    val id: Int,
    val customer: String,
    val total: Double,
    val status: String,
    @SerializedName("created_at") val createdAt: String?,
)

data class AdminOrdersResponse(
    val success: Boolean,
    val orders: List<AdminOrder> = emptyList(),
    val error: String?,
)

data class AdminPrayer(
    val id: Int,
    val name: String,
    val email: String?,
    val subject: String?,
    val prayer: String,
    @SerializedName("is_private") val isPrivate: Boolean,
    @SerializedName("submitted_at") val submittedAt: String?,
)

data class AdminPrayersResponse(
    val success: Boolean,
    val prayers: List<AdminPrayer> = emptyList(),
    val error: String?,
)

data class AdminSubscriber(
    val id: Int,
    val name: String,
    val email: String,
    val frequency: String,
    @SerializedName("subscribed_at") val subscribedAt: String?,
)

data class AdminSubscribersResponse(
    val success: Boolean,
    val subscribers: List<AdminSubscriber> = emptyList(),
    val error: String?,
)

package com.tdthanh.greenshop.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val points: Int = 0,
    val isVip: Boolean = false
)

data class Order(
    val id: String,
    val userId: String,
    val items: List<CartItem>,
    val totalAmount: Double,
    val status: OrderStatus,
    val createdAt: Long = System.currentTimeMillis(),
    val deliveryAddress: String
)

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PREPARING,
    DELIVERING,
    DELIVERED,
    CANCELLED
}

enum class PaymentMethod {
    CASH_ON_DELIVERY,
    CREDIT_CARD,
    DEBIT_CARD,
    BANK_TRANSFER,
    DIGITAL_WALLET
}

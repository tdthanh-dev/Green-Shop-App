package com.tdthanh.greenshop.model

data class CartItem(
    val product: Product,
    val quantity: Int = 1
) {
    val totalPrice: Double
        get() = product.price * quantity
}

data class CartState(
    val items: List<CartItem> = emptyList(),
    val deliveryFee: Double = 15000.0,
    val freeDeliveryThreshold: Double = 200000.0
) {
    val subtotal: Double
        get() = items.sumOf { it.totalPrice }
    
    val actualDeliveryFee: Double
        get() = if (subtotal >= freeDeliveryThreshold) 0.0 else deliveryFee
    
    val total: Double
        get() = subtotal + actualDeliveryFee
    
    val totalItems: Int
        get() = items.sumOf { it.quantity }
        
    val savings: Double
        get() = items.sumOf { item ->
            val originalPrice = item.product.originalPrice ?: item.product.price
            (originalPrice - item.product.price) * item.quantity
        }
}

data class User(
    val id: String = "tdthanh_001",
    val name: String = "TDThanh",
    val email: String = "tdthanh@greenshop.com",
    val phone: String = "0123456789",
    val address: String = "123 Đường ABC, Quận 1, TP.HCM",
    val isVip: Boolean = true,
    val points: Int = 850
)

data class Order(
    val id: String,
    val items: List<CartItem>,
    val total: Double,
    val status: OrderStatus,
    val createdAt: Long = System.currentTimeMillis(),
    val deliveryAddress: String,
    val paymentMethod: PaymentMethod
)

enum class OrderStatus(val displayName: String) {
    PENDING("Đang xử lý"),
    CONFIRMED("Đã xác nhận"),
    PREPARING("Đang chuẩn bị"),
    SHIPPING("Đang giao hàng"),
    DELIVERED("Đã giao hàng"),
    CANCELLED("Đã hủy")
}

enum class PaymentMethod(val displayName: String) {
    CASH("Tiền mặt"),
    CARD("Thẻ tín dụng"),
    BANKING("Chuyển khoản"),
    EWALLET("Ví điện tử")
}

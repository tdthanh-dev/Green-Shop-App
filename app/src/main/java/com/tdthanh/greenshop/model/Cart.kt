package com.tdthanh.greenshop.model

data class CartItem(
    val product: Product,
    val quantity: Int = 1
) {
    val totalPrice: Double
        get() = product.price * quantity
}

data class Cart(
    val items: List<CartItem> = emptyList()
) {
    val totalQuantity: Int
        get() = items.sumOf { it.quantity }
    
    val totalPrice: Double
        get() = items.sumOf { it.product.price * it.quantity }
        
    val subtotal: Double
        get() = totalPrice
        
    val shipping: Double
        get() = if (totalPrice > 200000) 0.0 else 30000.0
        
    val discount: Double
        get() = items.sumOf { item ->
            val originalPrice = item.product.originalPrice ?: item.product.price
            (originalPrice - item.product.price) * item.quantity
        }
        
    val finalTotal: Double
        get() = subtotal + shipping - discount
}

fun Cart.calculateTotalSavings(): Double {
    return items.sumOf { item ->
        val originalPrice = item.product.originalPrice ?: item.product.price
        (originalPrice - item.product.price) * item.quantity
    }
}

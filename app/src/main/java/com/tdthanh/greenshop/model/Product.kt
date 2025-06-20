package com.tdthanh.greenshop.model

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val originalPrice: Double? = null,
    val imageUrl: String,
    val category: ProductCategory,
    val unit: String = "kg",
    val inStock: Boolean = true,
    val rating: Float = 4.5f,
    val reviewCount: Int = 0,
    val isOrganic: Boolean = false,
    val isFresh: Boolean = true
)

enum class ProductCategory(val displayName: String, val emoji: String) {
    ALL("Tất cả", "🛒"),
    VEGETABLES("Rau củ", "🥬"),
    FRUITS("Trái cây", "🍎"),
    HERBS("Rau thơm", "🌿"),
    ORGANIC("Hữu cơ", "🌱"),
    SALADS("Salad", "🥗"),
    MUSHROOMS("Nấm", "🍄")
}

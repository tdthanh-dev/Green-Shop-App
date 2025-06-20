package com.tdthanh.greenshop.data

import com.tdthanh.greenshop.model.Product
import com.tdthanh.greenshop.model.ProductCategory

object ProductRepository {
    val products = listOf(
        Product(
            id = "1",
            name = "Cà chua bi organic",
            price = 45000.0,
            originalPrice = 55000.0,
            description = "Cà chua bi organic tươi ngon, không thuốc trừ sâu. Giàu vitamin C và lycopene tốt cho sức khỏe.",
            category = ProductCategory.VEGETABLES,
            imageUrl = "https://example.com/tomato.jpg",
            isOrganic = true,
            rating = 4.8f,
            reviewCount = 124,
            unit = "kg"
        ),
        Product(
            id = "2",
            name = "Rau muống thủy canh",
            price = 15000.0,
            description = "Rau muống thủy canh sạch, giòn ngọt. Trồng trong môi trường kiểm soát hoàn toàn.",
            category = ProductCategory.VEGETABLES,
            imageUrl = "https://example.com/spinach.jpg",
            rating = 4.6f,
            reviewCount = 89,
            unit = "bó"
        ),
        Product(
            id = "3",
            name = "Táo Fuji Nhật Bản",
            price = 120000.0,
            originalPrice = 150000.0,
            description = "Táo Fuji nhập khẩu từ Nhật Bản, giòn ngọt, thơm đậm đà hương vị truyền thống.",
            category = ProductCategory.FRUITS,
            imageUrl = "https://example.com/apple.jpg",
            rating = 4.9f,
            reviewCount = 256,
            unit = "kg"
        ),
        Product(
            id = "4",
            name = "Xoài cát Hòa Lộc",
            price = 85000.0,
            description = "Xoài cát Hòa Lộc đặc sản Tiền Giang, thịt vàng, ngọt đậm, thơm nức mũi.",
            category = ProductCategory.FRUITS,
            imageUrl = "https://example.com/mango.jpg",
            rating = 4.7f,
            reviewCount = 178,
            unit = "kg"
        ),
        Product(
            id = "5",
            name = "Rau thơm tổng hợp",
            price = 25000.0,
            description = "Combo rau thơm gồm: húng quế, ngò gai, kinh giới, tía tô. Tươi mới mỗi ngày.",
            category = ProductCategory.HERBS,
            imageUrl = "https://example.com/herbs.jpg",
            rating = 4.5f,
            reviewCount = 67,
            unit = "combo"
        ),
        Product(
            id = "6",
            name = "Súp lơ xanh organic",
            price = 35000.0,
            originalPrice = 42000.0,
            description = "Súp lơ xanh organic, giàu vitamin K và folate. Tốt cho tim mạch và xương khớp.",
            category = ProductCategory.ORGANIC,
            imageUrl = "https://example.com/broccoli.jpg",
            rating = 4.4f,
            reviewCount = 93,
            unit = "kg",
            isOrganic = true
        ),
        Product(
            id = "7",
            name = "Dưa hấu không hạt",
            price = 28000.0,
            description = "Dưa hấu ruột đỏ không hạt, ngọt mát, thích hợp cho mùa hè. Giải khát tuyệt vời.",
            category = ProductCategory.FRUITS,
            imageUrl = "https://example.com/watermelon.jpg",
            rating = 4.6f,
            reviewCount = 145,
            unit = "kg"
        ),
        Product(
            id = "8",
            name = "Cải thảo baby",
            price = 18000.0,
            description = "Cải thảo baby non tơi, lá xanh mướt. Thích hợp nấu canh, xào hoặc ăn sống.",
            category = ProductCategory.VEGETABLES,
            imageUrl = "https://example.com/cabbage.jpg",
            rating = 4.3f,
            reviewCount = 76,
            unit = "bó"
        ),
        Product(
            id = "9",
            name = "Nho Mỹ không hạt",
            price = 180000.0,
            originalPrice = 220000.0,
            description = "Nho đỏ Mỹ không hạt, ngọt thanh, giòn mọng nước. Bảo quản lạnh để giữ độ tươi ngon.",
            category = ProductCategory.FRUITS,
            imageUrl = "https://example.com/grapes.jpg",
            rating = 4.8f,
            reviewCount = 203,
            unit = "kg"
        ),
        Product(
            id = "10",
            name = "Rau má organic",
            price = 22000.0,
            description = "Rau má organic tươi, có tác dụng giải nhiệt, thanh lọc cơ thể. Phù hợp uống sinh tố.",
            category = ProductCategory.HERBS,
            imageUrl = "https://example.com/pennywort.jpg",
            rating = 4.2f,
            reviewCount = 54,
            unit = "bó",
            isOrganic = true
        ),
        Product(
            id = "11",
            name = "Salad trộn ready-to-eat",
            price = 32000.0,
            description = "Salad trộn sẵn với rau xà lách, cà rốt, cà chua cherry. Rửa sạch, ăn liền.",
            category = ProductCategory.SALADS,
            imageUrl = "https://example.com/salad.jpg",
            rating = 4.4f,
            reviewCount = 87,
            unit = "hộp"
        ),
        Product(
            id = "12",
            name = "Nấm shiitake tươi",
            price = 95000.0,
            description = "Nấm shiitake tươi nhập khẩu, thơm ngon bổ dưỡng. Giàu protein và vitamin D.",
            category = ProductCategory.MUSHROOMS,
            imageUrl = "https://example.com/shiitake.jpg",
            rating = 4.7f,
            reviewCount = 156,
            unit = "kg"
        )
    )

    fun getProductsByCategory(category: ProductCategory): List<Product> {
        return products.filter { it.category == category }
    }

    fun getProductById(id: String): Product? {
        return products.find { it.id == id }
    }

    fun searchProducts(query: String): List<Product> {
        return products.filter { 
            it.name.contains(query, ignoreCase = true) || 
            it.description.contains(query, ignoreCase = true) 
        }
    }

    fun getFeaturedProducts(): List<Product> {
        return products.filter { it.originalPrice != null || it.rating >= 4.7f }
    }

    fun getDiscountedProducts(): List<Product> {
        return products.filter { it.originalPrice != null }
    }

    fun getOrganicProducts(): List<Product> {
        return products.filter { it.isOrganic }
    }
}

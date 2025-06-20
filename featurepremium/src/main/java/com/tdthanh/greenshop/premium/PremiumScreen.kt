package com.tdthanh.greenshop.premium

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreen(
    onNavigateBack: () -> Unit = {},
    onUpgrade: () -> Unit = {}
) {
    var isVip by remember { mutableStateOf(false) }
    val premiumFeatures = remember { getPremiumFeatures() }
    val vipOffers = remember { getVipOffers() }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Premium VIP")
                        if (isVip) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "VIP",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isVip) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // VIP Status Card
            item {
                VipStatusCard(
                    isVip = isVip,
                    onUpgrade = { 
                        isVip = true
                        onUpgrade()
                    }
                )
            }
            
            // Premium Features
            item {
                Text(
                    text = "Đặc quyền VIP",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            items(premiumFeatures) { feature ->
                PremiumFeatureCard(
                    feature = feature,
                    isUnlocked = isVip
                )
            }
            
            // VIP Exclusive Offers
            if (isVip) {
                item {
                    Text(
                        text = "Ưu đãi độc quyền VIP",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                items(vipOffers) { offer ->
                    VipOfferCard(offer)
                }
            }
            
            // Subscription Plans
            if (!isVip) {
                item {
                    Text(
                        text = "Gói đăng ký",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                item {
                    SubscriptionPlansSection(
                        onSubscribe = { 
                            isVip = true
                            onUpgrade()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun VipStatusCard(
    isVip: Boolean,
    onUpgrade: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isVip) {
                Color.Transparent
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Box(
            modifier = if (isVip) {
                Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFFFD700),
                                Color(0xFFFFA000)
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
            } else {
                Modifier.fillMaxWidth()
            }
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {                AnimatedContent(
                    targetState = isVip,
                    label = "vip_status"
                ) { vipStatus ->
                    if (vipStatus) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "VIP",
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text(
                                text = "Chúc mừng! Bạn là thành viên VIP",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            
                            Text(
                                text = "Tận hưởng tất cả đặc quyền cao cấp",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Build,
                                contentDescription = "Premium",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text(
                                text = "Nâng cấp lên VIP",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Text(
                                text = "Mở khóa tất cả tính năng cao cấp và ưu đãi độc quyền",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = onUpgrade,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Nâng cấp ngay")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PremiumFeatureCard(
    feature: PremiumFeature,
    isUnlocked: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = feature.icon,
                    contentDescription = null,
                    tint = if (isUnlocked) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = feature.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    if (isUnlocked) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Đã mở khóa",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                Text(
                    text = feature.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                if (feature.savings != null) {
                    Text(
                        text = "Tiết kiệm ${feature.savings}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            
            if (!isUnlocked) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = "Bị khóa",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun VipOfferCard(offer: VipOffer) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = offer.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                AssistChip(
                    onClick = { },
                    label = { Text("VIP") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color(0xFFFFD700),
                        labelColor = Color.Black
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = offer.description,
                style = MaterialTheme.typography.bodyMedium
            )
            
            if (offer.originalPrice != null) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                            .format(offer.vipPrice),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                            .format(offer.originalPrice),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                    )
                }
            }
        }
    }
}

@Composable
private fun SubscriptionPlansSection(onSubscribe: () -> Unit) {
    val plans = listOf(
        SubscriptionPlan(
            name = "VIP Tháng",
            price = 99000,
            duration = "1 tháng",
            features = listOf("Miễn phí giao hàng", "Giảm giá 15%", "Hỗ trợ ưu tiên"),
            isPopular = false
        ),
        SubscriptionPlan(
            name = "VIP Năm",
            price = 990000,
            duration = "12 tháng",
            features = listOf("Miễn phí giao hàng", "Giảm giá 20%", "Hỗ trợ ưu tiên", "Quà tặng sinh nhật"),
            isPopular = true,
            savings = "Tiết kiệm 198.000₫"
        )
    )
    
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        plans.forEach { plan ->
            SubscriptionPlanCard(
                plan = plan,
                onSubscribe = onSubscribe
            )
        }
    }
}

@Composable
private fun SubscriptionPlanCard(
    plan: SubscriptionPlan,
    onSubscribe: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (plan.isPopular) {
                    Modifier.border(
                        2.dp,
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(12.dp)
                    )
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (plan.isPopular) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = plan.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                if (plan.isPopular) {
                    Spacer(modifier = Modifier.width(8.dp))
                    AssistChip(
                        onClick = { },
                        label = { Text("Phổ biến") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            labelColor = Color.White
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                        .format(plan.price),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "/${plan.duration}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (plan.savings != null) {
                Text(
                    text = plan.savings,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            plan.features.forEach { feature ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(16.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onSubscribe,
                modifier = Modifier.fillMaxWidth(),
                colors = if (plan.isPopular) {
                    ButtonDefaults.buttonColors()
                } else {
                    ButtonDefaults.outlinedButtonColors()
                }
            ) {
                Text(if (plan.isPopular) "Chọn gói này" else "Đăng ký")
            }
        }
    }
}

// Data classes
data class PremiumFeature(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val savings: String? = null
)

data class VipOffer(
    val title: String,
    val description: String,
    val vipPrice: Long,
    val originalPrice: Long? = null
)

data class SubscriptionPlan(
    val name: String,
    val price: Long,
    val duration: String,
    val features: List<String>,
    val isPopular: Boolean = false,
    val savings: String? = null
)

// Sample data
private fun getPremiumFeatures(): List<PremiumFeature> {
    return listOf(
        PremiumFeature(
            title = "Miễn phí giao hàng",
            description = "Giao hàng miễn phí cho tất cả đơn hàng, không giới hạn số lượng",
            icon = Icons.Default.ShoppingCart,
            savings = "~50.000₫/tháng"
        ),
        PremiumFeature(
            title = "Giảm giá độc quyền",
            description = "Giảm giá từ 15-20% cho tất cả sản phẩm, áp dụng cả sale",
            icon = Icons.Default.Star,
            savings = "Lên đến 30%"
        ),
        PremiumFeature(
            title = "Ưu tiên đặt hàng",
            description = "Được ưu tiên chọn sản phẩm tươi ngon nhất và giao hàng nhanh nhất",
            icon = Icons.Default.Settings,
        ),
        PremiumFeature(
            title = "Hỗ trợ 24/7",
            description = "Đội ngũ chăm sóc khách hàng VIP hỗ trợ bạn mọi lúc mọi nơi",
            icon = Icons.Default.Info,
        ),
        PremiumFeature(
            title = "Truy cập sớm",
            description = "Được mua sản phẩm mới và tham gia flash sale trước 24h",
            icon = Icons.Default.Notifications,
        ),
        PremiumFeature(
            title = "Quà tặng đặc biệt",
            description = "Nhận quà tặng, voucher và ưu đãi độc quyền vào các dịp đặc biệt",
            icon = Icons.Default.Settings,
        )
    )
}

private fun getVipOffers(): List<VipOffer> {
    return listOf(
        VipOffer(
            title = "Set rau củ hữu cơ cao cấp",
            description = "Combo 5kg rau củ hữu cơ nhập khẩu từ Đà Lạt",
            vipPrice = 299000,
            originalPrice = 450000
        ),
        VipOffer(
            title = "Trái cây nhập khẩu Premium",
            description = "Táo Fuji, cam Úc, nho đỏ không hạt - 3kg",
            vipPrice = 199000,
            originalPrice = 299000
        ),
        VipOffer(
            title = "Bộ gia vị organic",
            description = "15 loại gia vị hữu cơ tự nhiên từ các vùng miền",
            vipPrice = 159000,
            originalPrice = 230000
        )
    )
}

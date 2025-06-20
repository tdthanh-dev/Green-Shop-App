package com.tdthanh.greenshop.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onNavigateBack: () -> Unit = {}
) {
    val analyticsData = remember { generateAnalyticsData() }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Phân tích nâng cao") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Export data */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Xuất dữ liệu")
                    }
                }
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
            // Tổng quan
            item {
                OverviewSection(analyticsData.overview)
            }
            
            // Biểu đồ doanh thu
            item {
                RevenueChartSection(analyticsData.revenueData)
            }
            
            // Sản phẩm bán chạy
            item {
                TopProductsSection(analyticsData.topProducts)
            }
            
            // Phân tích khách hàng
            item {
                CustomerAnalyticsSection(analyticsData.customerData)
            }
            
            // Xu hướng thời gian
            item {
                TimeAnalyticsSection(analyticsData.timeData)
            }
        }
    }
}

@Composable
private fun OverviewSection(overview: OverviewData) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Tổng quan tháng này",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(overview.metrics) { metric ->
                    MetricCard(metric)
                }
            }
        }
    }
}

@Composable
private fun MetricCard(metric: MetricData) {
    Card(
        modifier = Modifier.width(140.dp),
        colors = CardDefaults.cardColors(
            containerColor = metric.color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = metric.icon,
                contentDescription = null,
                tint = metric.color,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = metric.value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = metric.label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (metric.change != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (metric.change > 0) Icons.Default.Add else Icons.Default.Clear,
                        contentDescription = null,
                        tint = if (metric.change > 0) Color(0xFF4CAF50) else Color(0xFFF44336),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${if (metric.change > 0) "+" else ""}${metric.change}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (metric.change > 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                }
            }
        }
    }
}

@Composable
private fun RevenueChartSection(revenueData: List<RevenuePoint>) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Doanh thu 7 ngày qua",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Simple bar chart
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                revenueData.forEach { point ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = NumberFormat.getInstance(Locale("vi", "VN"))
                                .format(point.amount),
                            style = MaterialTheme.typography.bodySmall
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height((point.amount / 1000000 * 100).toFloat().coerceAtMost(120f).dp)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = point.day,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopProductsSection(topProducts: List<ProductAnalytics>) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Top sản phẩm bán chạy",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            topProducts.forEachIndexed { index, product ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rank
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                when (index) {
                                    0 -> Color(0xFFFFD700)
                                    1 -> Color(0xFFC0C0C0)
                                    2 -> Color(0xFFCD7F32)
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (index < 3) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${product.soldQuantity} đã bán",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Text(
                        text = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                            .format(product.revenue),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                if (index < topProducts.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomerAnalyticsSection(customerData: CustomerAnalyticsData) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Phân tích khách hàng",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CustomerMetric(
                    label = "Khách hàng mới",
                    value = customerData.newCustomers.toString(),
                    color = Color(0xFF4CAF50)
                )
                CustomerMetric(
                    label = "Khách quay lại",
                    value = customerData.returningCustomers.toString(),
                    color = Color(0xFF2196F3)
                )
                CustomerMetric(
                    label = "Tỷ lệ giữ chân",
                    value = "${customerData.retentionRate}%",
                    color = Color(0xFFFF9800)
                )
            }
        }
    }
}

@Composable
private fun CustomerMetric(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TimeAnalyticsSection(timeData: TimeAnalyticsData) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Xu hướng theo thời gian",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Giờ bán hàng cao nhất: ${timeData.peakHour}:00",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Ngày trong tuần bán chạy nhất: ${timeData.peakDay}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Trung bình đơn hàng/ngày: ${timeData.avgOrdersPerDay}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// Data classes
data class OverviewData(
    val metrics: List<MetricData>
)

data class MetricData(
    val label: String,
    val value: String,
    val icon: ImageVector,
    val color: Color,
    val change: Int? = null
)

data class RevenuePoint(
    val day: String,
    val amount: Long
)

data class ProductAnalytics(
    val name: String,
    val soldQuantity: Int,
    val revenue: Long
)

data class CustomerAnalyticsData(
    val newCustomers: Int,
    val returningCustomers: Int,
    val retentionRate: Int
)

data class TimeAnalyticsData(
    val peakHour: Int,
    val peakDay: String,
    val avgOrdersPerDay: Int
)

// Sample data generator
private fun generateAnalyticsData(): AnalyticsData {
    return AnalyticsData(
        overview = OverviewData(
            metrics = listOf(
                MetricData(
                    label = "Doanh thu",
                    value = "₫12.5M",
                    icon = Icons.Default.Star,
                    color = Color(0xFF4CAF50),
                    change = 15
                ),
                MetricData(
                    label = "Đơn hàng",
                    value = "248",
                    icon = Icons.Default.ShoppingCart,
                    color = Color(0xFF2196F3),
                    change = 8
                ),
                MetricData(
                    label = "Khách hàng",
                    value = "156",
                    icon = Icons.Default.Person,
                    color = Color(0xFFFF9800),
                    change = 23
                ),
                MetricData(
                    label = "Tỷ lệ chuyển đổi",
                    value = "3.2%",
                    icon = Icons.Default.Add,
                    color = Color(0xFF9C27B0),
                    change = -2
                )
            )
        ),
        revenueData = listOf(
            RevenuePoint("T2", 1800000),
            RevenuePoint("T3", 2200000),
            RevenuePoint("T4", 1900000),
            RevenuePoint("T5", 2400000),
            RevenuePoint("T6", 2800000),
            RevenuePoint("T7", 3200000),
            RevenuePoint("CN", 2100000)
        ),
        topProducts = listOf(
            ProductAnalytics("Rau cải xanh hữu cơ", 45, 675000),
            ProductAnalytics("Cà chua bi Đà Lạt", 38, 570000),
            ProductAnalytics("Táo Fuji Nhật Bản", 32, 960000),
            ProductAnalytics("Nấm kim châm", 28, 420000),
            ProductAnalytics("Rau muống sạch", 25, 250000)
        ),
        customerData = CustomerAnalyticsData(
            newCustomers = 45,
            returningCustomers = 111,
            retentionRate = 72
        ),
        timeData = TimeAnalyticsData(
            peakHour = 19,
            peakDay = "Thứ 7",
            avgOrdersPerDay = 35
        )
    )
}

data class AnalyticsData(
    val overview: OverviewData,
    val revenueData: List<RevenuePoint>,
    val topProducts: List<ProductAnalytics>,
    val customerData: CustomerAnalyticsData,
    val timeData: TimeAnalyticsData
)

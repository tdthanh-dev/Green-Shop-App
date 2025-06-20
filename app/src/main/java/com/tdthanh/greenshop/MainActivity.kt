package com.tdthanh.greenshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.tdthanh.greenshop.data.ProductRepository
import com.tdthanh.greenshop.feature.FeatureDownloadManager
import com.tdthanh.greenshop.feature.DynamicFeatureEntryFactory
import com.tdthanh.greenshop.model.Product
import com.tdthanh.greenshop.ui.screens.*
import com.tdthanh.greenshop.ui.theme.GreenShopTheme
import com.tdthanh.greenshop.viewmodel.GreenShopViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            GreenShopTheme {
                GreenShopApp()
            }
        }
    }
}

@Composable
fun GreenShopApp() {
    val navController = rememberNavController()
    val viewModel: GreenShopViewModel = viewModel()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        GreenShopNavHost(
            navController = navController,
            viewModel = viewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun GreenShopNavHost(
    navController: NavHostController,
    viewModel: GreenShopViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onProductClick = { product ->
                    navController.navigate("product_detail/${product.id}")
                },
                onCartClick = {
                    navController.navigate("cart")
                },
                onFeaturesClick = {
                    navController.navigate("features")
                }
            )
        }
          composable("features") {
            FeaturesScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToFeature = { featureName ->
                    when (featureName) {
                        "featureanalytics" -> navController.navigate("analytics")
                        "featurepremium" -> navController.navigate("premium")
                        "featureadvancedsearch" -> navController.navigate("advanced_search")
                    }
                }
            )
        }
        
        composable("cart") {
            CartScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("product_detail/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            val product = ProductRepository.getProductById(productId ?: "")
            
            if (product != null) {
                ProductDetailScreen(
                    product = product,
                    viewModel = viewModel,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
          // Dynamic feature routes - load thật sự UI từ dynamic features
        composable("analytics") {
            LoadDynamicFeatureScreen(
                featureName = "featureanalytics",
                className = "com.tdthanh.greenshop.analytics.AnalyticsScreen",
                displayName = "Analytics",
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable("premium") {
            LoadDynamicFeatureScreen(
                featureName = "featurepremium", 
                className = "com.tdthanh.greenshop.premium.PremiumScreen",
                displayName = "Premium",
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable("advanced_search") {
            LoadDynamicFeatureScreen(
                featureName = "featureadvancedsearch",
                className = "com.tdthanh.greenshop.search.AdvancedSearchScreen", 
                displayName = "Advanced Search",
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun LoadDynamicFeatureScreen(
    featureName: String,
    className: String,
    displayName: String,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val featureManager = remember { FeatureDownloadManager(context) }
    
    if (featureManager.isFeatureInstalled(featureName)) {
        // Thử load UI thực tế từ dynamic feature
        val featureEntry = remember { DynamicFeatureEntryFactory.getEntryForFeature(featureName) }
        
        if (featureEntry != null) {
            // Load UI thực tế từ dynamic feature
            featureEntry.Content(onNavigateBack = onNavigateBack)
        } else {
            // Fallback to placeholder nếu không load được
            DynamicFeatureLoadedScreen(displayName, onNavigateBack)
        }
    } else {
        // Nếu feature chưa được cài đặt
        FeatureNotInstalledScreen(displayName, onNavigateBack)
    }
}

@Composable
fun DynamicFeatureLoadedScreen(
    featureName: String,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Extension,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "🎉 $featureName đã tải thành công!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Dynamic feature $featureName đã được tải về và cài đặt thành công. Trong ứng dụng thực tế, đây sẽ là UI của feature này.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                // Mô phỏng feature content
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "📊 $featureName Content",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = when (featureName) {
                                "Analytics" -> "• Thống kê doanh thu\n• Báo cáo khách hàng\n• Phân tích xu hướng"
                                "Premium" -> "• Giảm giá đặc biệt\n• Giao hàng miễn phí\n• Hỗ trợ VIP"
                                "Advanced Search" -> "• Tìm kiếm bằng giọng nói\n• Tìm kiếm bằng hình ảnh\n• AI gợi ý sản phẩm"
                                else -> "• Tính năng đặc biệt\n• Chức năng nâng cao"
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Quay lại")
                }
            }
        }
    }
}

@Composable
fun FeatureNotInstalledScreen(
    featureName: String,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Extension,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Tính năng chưa được cài đặt",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Vui lòng tải xuống tính năng $featureName từ trang Quản lý tính năng trước khi sử dụng.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Quay lại")
                }
            }
        }
    }
}

@Composable
fun DynamicFeaturePlaceholder(
    featureName: String,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Extension,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "$featureName Feature",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tính năng này đã được tải về và sẵn sàng sử dụng. UI dynamic feature đang load...",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Quay lại")
                }
            }
        }
    }
}

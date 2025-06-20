package com.tdthanh.greenshop.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tdthanh.greenshop.model.Product
import com.tdthanh.greenshop.model.ProductCategory
import com.tdthanh.greenshop.viewmodel.GreenShopViewModel
import com.tdthanh.greenshop.ui.components.ProductCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    category: ProductCategory,
    viewModel: GreenShopViewModel,
    onProductClick: (Product) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsStateWithLifecycle()
    val categoryProducts = products.filter { it.category == category }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "${category.emoji} ${category.displayName}",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categoryProducts) { product ->
                ProductCard(
                    product = product,
                    onProductClick = onProductClick,
                    onAddToCart = { viewModel.addToCart(product) }
                )
            }
        }
    }
}

package com.tdthanh.greenshop.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
    viewModel: GreenShopViewModel,
    onProductClick: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val categories = ProductCategory.values()
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Header
        TopAppBar(
            title = { 
                Text(
                    text = "Danh mục sản phẩm",
                    fontWeight = FontWeight.Bold
                )
            }
        )
        
        // Category Filter
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    onClick = { viewModel.selectCategory(category) },
                    label = {
                        Text("${category.emoji} ${category.displayName}")
                    },
                    selected = selectedCategory == category
                )
            }
        }
        
        // Products Grid
        val filteredProducts = if (selectedCategory == ProductCategory.ALL) {
            products
        } else {
            products.filter { it.category == selectedCategory }
        }
          LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredProducts) { product ->
                ProductCard(
                    product = product,
                    onProductClick = onProductClick,
                    onAddToCart = { viewModel.addToCart(product) }
                )
            }
        }
    }
}

package com.tdthanh.greenshop.search

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedSearchScreen(
    onNavigateBack: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var searchMode by remember { mutableStateOf(SearchMode.TEXT) }
    var isSearching by remember { mutableStateOf(false) }
    var searchResults by remember { mutableStateOf<List<SearchResult>>(emptyList()) }
    var searchFilters by remember { mutableStateOf(SearchFilters()) }
    
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty()) {
            isSearching = true
            delay(1000) // Simulate AI processing
            searchResults = performAdvancedSearch(searchQuery, searchMode, searchFilters)
            isSearching = false
        } else {
            searchResults = emptyList()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tìm kiếm AI") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Voice search */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Tìm kiếm bằng giọng nói")
                    }
                    IconButton(onClick = { /* Camera search */ }) {
                        Icon(Icons.Default.Add, contentDescription = "Tìm kiếm bằng hình ảnh")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Input Section
            SearchInputSection(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                searchMode = searchMode,
                onSearchModeChange = { searchMode = it }
            )
            
            // Search Filters
            SearchFiltersSection(
                filters = searchFilters,
                onFiltersChange = { searchFilters = it }
            )
            
            // Search Results
            SearchResultsSection(
                isSearching = isSearching,
                results = searchResults,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SearchInputSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    searchMode: SearchMode,
    onSearchModeChange: (SearchMode) -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        // Search Mode Tabs
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            items(SearchMode.values()) { mode ->
                SearchModeChip(
                    mode = mode,
                    isSelected = mode == searchMode,
                    onClick = { onSearchModeChange(mode) }
                )
            }
        }
        
        // Search TextField
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { 
                Text(
                    when (searchMode) {
                        SearchMode.TEXT -> "Tìm kiếm sản phẩm..."
                        SearchMode.VOICE -> "Nói: 'Tôi muốn mua rau xanh tươi'"
                        SearchMode.IMAGE -> "Chụp ảnh hoặc tải lên hình ảnh"
                        SearchMode.AI_ASSISTANT -> "Hỏi AI: 'Món ăn gì phù hợp với cà chua?'"
                    }
                )
            },
            leadingIcon = {                Icon(
                    when (searchMode) {
                        SearchMode.TEXT -> Icons.Default.Search
                        SearchMode.VOICE -> Icons.Default.Settings
                        SearchMode.IMAGE -> Icons.Default.Add
                        SearchMode.AI_ASSISTANT -> Icons.Default.Star
                    },
                    contentDescription = null
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Xóa")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        
        // AI Suggestions
        if (searchMode == SearchMode.AI_ASSISTANT && searchQuery.isEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                items(getAISuggestions()) { suggestion ->
                    SuggestionChip(
                        onClick = { onSearchQueryChange(suggestion) },
                        label = { Text(suggestion) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchModeChip(
    mode: SearchMode,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        onClick = onClick,
        label = { Text(mode.displayName) },
        selected = isSelected,
        leadingIcon = {
            Icon(
                mode.icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    )
}

@Composable
private fun SearchFiltersSection(
    filters: SearchFilters,
    onFiltersChange: (SearchFilters) -> Unit
) {
    var showFilters by remember { mutableStateOf(false) }
    
    Column {
        // Filter Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showFilters = !showFilters }
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Bộ lọc",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Bộ lọc nâng cao",
                    style = MaterialTheme.typography.titleMedium
                )
                
                val activeFiltersCount = filters.getActiveFiltersCount()
                if (activeFiltersCount > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Badge {
                        Text(activeFiltersCount.toString())
                    }
                }
            }
            
            Icon(
                if (showFilters) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null
            )
        }
        
        // Filters Content
        AnimatedVisibility(
            visible = showFilters,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Price Range
                    Column {
                        Text(
                            text = "Khoảng giá",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium
                        )
                        
                        RangeSlider(
                            value = filters.priceRange,
                            onValueChange = { 
                                onFiltersChange(filters.copy(priceRange = it))
                            },
                            valueRange = 0f..1000000f,
                            steps = 20
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                                    .format(filters.priceRange.start.toLong()),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                                    .format(filters.priceRange.endInclusive.toLong()),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    
                    // Categories
                    Column {
                        Text(
                            text = "Danh mục",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium
                        )
                        
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(ProductCategory.values()) { category ->
                                FilterChip(
                                    onClick = {
                                        val newCategories = if (category in filters.categories) {
                                            filters.categories - category
                                        } else {
                                            filters.categories + category
                                        }
                                        onFiltersChange(filters.copy(categories = newCategories))
                                    },
                                    label = { Text("${category.emoji} ${category.displayName}") },
                                    selected = category in filters.categories
                                )
                            }
                        }
                    }
                    
                    // Special Filters
                    Column {
                        Text(
                            text = "Đặc biệt",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                onClick = {
                                    onFiltersChange(filters.copy(organicOnly = !filters.organicOnly))
                                },
                                label = { Text("Hữu cơ") },
                                selected = filters.organicOnly,
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                            
                            FilterChip(
                                onClick = {
                                    onFiltersChange(filters.copy(freshOnly = !filters.freshOnly))
                                },
                                label = { Text("Tươi sống") },
                                selected = filters.freshOnly,
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                            
                            FilterChip(
                                onClick = {
                                    onFiltersChange(filters.copy(onSaleOnly = !filters.onSaleOnly))
                                },
                                label = { Text("Giảm giá") },
                                selected = filters.onSaleOnly,
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                        }
                    }
                    
                    // Clear Filters
                    OutlinedButton(
                        onClick = { onFiltersChange(SearchFilters()) },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Xóa bộ lọc")
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultsSection(
    isSearching: Boolean,
    results: List<SearchResult>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        when {
            isSearching -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("AI đang tìm kiếm...")
                }
            }
            
            results.isEmpty() -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Không tìm thấy kết quả",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Hãy thử từ khóa khác hoặc điều chỉnh bộ lọc",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "Tìm thấy ${results.size} kết quả",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    items(results) { result ->
                        SearchResultCard(result)
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultCard(result: SearchResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { /* Navigate to product detail */ }
    ) {
        Row(
            modifier = Modifier.padding(12.dp)
        ) {
            // Product Image Placeholder
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = result.category.emoji,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = result.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (result.description.isNotEmpty()) {
                    Text(
                        text = result.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                            .format(result.price),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    if (result.originalPrice != null && result.originalPrice > result.price) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                                .format(result.originalPrice),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                        )
                    }
                }
                
                // Tags
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (result.isOrganic) {
                        AssistChip(
                            onClick = { },
                            label = { Text("Hữu cơ") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            modifier = Modifier.height(28.dp)
                        )
                    }
                    
                    if (result.isFresh) {
                        AssistChip(
                            onClick = { },
                            label = { Text("Tươi") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            modifier = Modifier.height(28.dp)
                        )
                    }
                }
            }
            
            // AI Match Score
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                result.aiMatchScore >= 90 -> Color(0xFF4CAF50)
                                result.aiMatchScore >= 70 -> Color(0xFFFF9800)
                                else -> Color(0xFFF44336)
                            }.copy(alpha = 0.1f)
                        )
                        .border(
                            2.dp,
                            when {
                                result.aiMatchScore >= 90 -> Color(0xFF4CAF50)
                                result.aiMatchScore >= 70 -> Color(0xFFFF9800)
                                else -> Color(0xFFF44336)
                            },
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${result.aiMatchScore}%",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            result.aiMatchScore >= 90 -> Color(0xFF4CAF50)
                            result.aiMatchScore >= 70 -> Color(0xFFFF9800)
                            else -> Color(0xFFF44336)
                        }
                    )
                }
                
                Text(
                    text = "AI Match",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

// Enums and Data Classes
enum class SearchMode(val displayName: String, val icon: ImageVector) {
    TEXT("Văn bản", Icons.Default.Search),    VOICE("Giọng nói", Icons.Default.Settings),
    IMAGE("Hình ảnh", Icons.Default.Add),
    AI_ASSISTANT("AI Assistant", Icons.Default.Star)
}

enum class ProductCategory(val displayName: String, val emoji: String) {
    VEGETABLES("Rau củ", "🥬"),
    FRUITS("Trái cây", "🍎"),
    HERBS("Rau thơm", "🌿"),
    ORGANIC("Hữu cơ", "🌱"),
    SALADS("Salad", "🥗"),
    MUSHROOMS("Nấm", "🍄")
}

data class SearchFilters(
    val priceRange: ClosedFloatingPointRange<Float> = 0f..1000000f,
    val categories: Set<ProductCategory> = emptySet(),
    val organicOnly: Boolean = false,
    val freshOnly: Boolean = false,
    val onSaleOnly: Boolean = false
) {
    fun getActiveFiltersCount(): Int {
        var count = 0
        if (priceRange != 0f..1000000f) count++
        if (categories.isNotEmpty()) count++
        if (organicOnly) count++
        if (freshOnly) count++
        if (onSaleOnly) count++
        return count
    }
}

data class SearchResult(
    val id: String,
    val name: String,
    val description: String,
    val price: Long,
    val originalPrice: Long? = null,
    val category: ProductCategory,
    val isOrganic: Boolean,
    val isFresh: Boolean,
    val aiMatchScore: Int // 0-100
)

// Helper functions
private fun getAISuggestions(): List<String> {
    return listOf(
        "Món ăn với cà chua",
        "Rau củ giàu vitamin C",
        "Nguyên liệu làm salad",
        "Thực phẩm tốt cho da",
        "Rau cho bé ăn dặm"
    )
}

private fun performAdvancedSearch(
    query: String,
    mode: SearchMode,
    filters: SearchFilters
): List<SearchResult> {
    // Simulate AI search results
    val allResults = listOf(
        SearchResult(
            id = "1",
            name = "Rau cải xanh hữu cơ",
            description = "Rau cải xanh tươi ngon, không thuốc trừ sâu",
            price = 25000,
            originalPrice = 30000,
            category = ProductCategory.VEGETABLES,
            isOrganic = true,
            isFresh = true,
            aiMatchScore = 95
        ),
        SearchResult(
            id = "2",
            name = "Cà chua bi Đà Lạt",
            description = "Cà chua bi ngọt, thích hợp ăn sống",
            price = 45000,
            category = ProductCategory.VEGETABLES,
            isOrganic = false,
            isFresh = true,
            aiMatchScore = 88
        ),
        SearchResult(
            id = "3",
            name = "Táo Fuji Nhật Bản",
            description = "Táo nhập khẩu, giòn ngọt, giàu vitamin",
            price = 120000,
            originalPrice = 150000,
            category = ProductCategory.FRUITS,
            isOrganic = true,
            isFresh = true,
            aiMatchScore = 92
        ),
        SearchResult(
            id = "4",
            name = "Nấm kim châm",
            description = "Nấm tươi, bổ dưỡng cho gia đình",
            price = 35000,
            category = ProductCategory.MUSHROOMS,
            isOrganic = false,
            isFresh = true,
            aiMatchScore = 75
        ),
        SearchResult(
            id = "5",
            name = "Rau muống sạch",
            description = "Rau muống an toàn, không hóa chất",
            price = 15000,
            category = ProductCategory.VEGETABLES,
            isOrganic = true,
            isFresh = true,
            aiMatchScore = 85
        )
    )
    
    return allResults.filter { result ->
        // Apply filters
        val matchesPrice = result.price.toFloat() in filters.priceRange
        val matchesCategory = filters.categories.isEmpty() || result.category in filters.categories
        val matchesOrganic = !filters.organicOnly || result.isOrganic
        val matchesFresh = !filters.freshOnly || result.isFresh
        val matchesSale = !filters.onSaleOnly || (result.originalPrice != null && result.originalPrice > result.price)
        val matchesQuery = result.name.contains(query, ignoreCase = true) || 
                          result.description.contains(query, ignoreCase = true)
        
        matchesPrice && matchesCategory && matchesOrganic && matchesFresh && matchesSale && matchesQuery
    }.sortedByDescending { it.aiMatchScore }
}

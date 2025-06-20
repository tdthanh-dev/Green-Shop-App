package com.tdthanh.greenshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tdthanh.greenshop.data.ProductRepository
import com.tdthanh.greenshop.model.CartItem
import com.tdthanh.greenshop.model.Product
import com.tdthanh.greenshop.model.ProductCategory
import com.tdthanh.greenshop.model.User
import com.tdthanh.greenshop.model.Order
import com.tdthanh.greenshop.model.OrderStatus
import com.tdthanh.greenshop.model.PaymentMethod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GreenShopViewModel : ViewModel() {
    
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()
    
    private val _featuredProducts = MutableStateFlow<List<Product>>(emptyList())
    val featuredProducts: StateFlow<List<Product>> = _featuredProducts.asStateFlow()
    
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()
    
    private val _selectedCategory = MutableStateFlow<ProductCategory?>(null)
    val selectedCategory: StateFlow<ProductCategory?> = _selectedCategory.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // User and orders data
    private val _currentUser = MutableStateFlow(User())
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()
    
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    init {
        loadProducts()
        loadFeaturedProducts()
        loadUserOrders()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _products.value = ProductRepository.products
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadFeaturedProducts() {
        viewModelScope.launch {
            _featuredProducts.value = ProductRepository.getFeaturedProducts()
        }
    }

    private fun loadUserOrders() {
        viewModelScope.launch {
            // Sample orders data - in real app, this would come from repository
            _orders.value = emptyList() // You can add sample orders here if needed
        }
    }

    fun filterByCategory(category: ProductCategory?) {
        _selectedCategory.value = category
        viewModelScope.launch {
            _products.value = if (category != null) {
                ProductRepository.getProductsByCategory(category)
            } else {
                ProductRepository.products
            }
        }
    }

    fun searchProducts(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            _products.value = if (query.isBlank()) {
                if (_selectedCategory.value != null) {
                    ProductRepository.getProductsByCategory(_selectedCategory.value!!)
                } else {
                    ProductRepository.products
                }
            } else {
                ProductRepository.searchProducts(query)
            }
        }
    }

    fun addToCart(product: Product, quantity: Int = 1) {
        viewModelScope.launch {
            val currentCart = _cartItems.value.toMutableList()
            val existingItem = currentCart.find { it.product.id == product.id }
            
            if (existingItem != null) {
                // Update quantity
                val updatedItem = existingItem.copy(quantity = existingItem.quantity + quantity)
                currentCart[currentCart.indexOf(existingItem)] = updatedItem
            } else {
                // Add new item
                currentCart.add(CartItem(product, quantity))
            }
            
            _cartItems.value = currentCart
        }
    }

    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            _cartItems.value = _cartItems.value.filter { it.product.id != productId }
        }
    }

    fun updateCartItemQuantity(productId: String, quantity: Int) {
        viewModelScope.launch {
            val currentCart = _cartItems.value.toMutableList()
            val itemIndex = currentCart.indexOfFirst { it.product.id == productId }
            
            if (itemIndex != -1) {
                if (quantity <= 0) {
                    currentCart.removeAt(itemIndex)
                } else {
                    currentCart[itemIndex] = currentCart[itemIndex].copy(quantity = quantity)
                }
                _cartItems.value = currentCart
            }
        }
    }

    fun getTotalCartPrice(): Double {
        return _cartItems.value.sumOf { it.totalPrice }
    }

    fun getCartItemCount(): Int {
        return _cartItems.value.sumOf { it.quantity }
    }    fun clearCart() {
        _cartItems.value = emptyList()
    }
}

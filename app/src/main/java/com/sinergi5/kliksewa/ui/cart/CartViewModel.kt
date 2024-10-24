package com.sinergi5.kliksewa.ui.cart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinergi5.kliksewa.data.model.CartItem
import com.sinergi5.kliksewa.helper.UiState
import com.sinergi5.kliksewa.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CartViewModel(private val repository: Repository) : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadCartItems() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getCartItems()
                .onSuccess { items ->
                    _cartItems.value = items
                }
                .onFailure { exception ->
                    // Handle error
                }
            _isLoading.value = false
        }
    }


    fun updateQuantity(cartId: String, quantity: Int) {
        viewModelScope.launch {
            repository.updateCartItemQuantity(cartId, quantity)
                .onSuccess {
                    loadCartItems() // Reload cart items
                }
                .onFailure { exception ->
                    // Handle error
                }
        }
    }

    fun removeItem(cartId: String) {
        viewModelScope.launch {
            repository.removeCartItem(cartId)
                .onSuccess {
                    loadCartItems() // Reload cart items
                }
                .onFailure { exception ->
                    // Handle error
                }
        }
    }

}
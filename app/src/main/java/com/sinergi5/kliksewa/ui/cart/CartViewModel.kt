package com.sinergi5.kliksewa.ui.cart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinergi5.kliksewa.data.model.CartItem
import com.sinergi5.kliksewa.helper.UiState
import com.sinergi5.kliksewa.repository.Repository
import kotlinx.coroutines.launch

class CartViewModel(private val repository: Repository) : ViewModel() {

    private val _uiState = MutableLiveData<UiState<List<CartItem>>>()
    val uiState: LiveData<UiState<List<CartItem>>> = _uiState

    init {
        // Fetch cart items when ViewModel is initialized
        getCartItems()  // Assume `getUserId()` retrieves the current user ID
    }

    private fun getCartItems() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val result = repository.getCartItems()
                result.fold(
                    onSuccess = { cartItems ->
                        _uiState.value = UiState.Success(cartItems)
                        Log.d("CartViewModel", "Cart items fetched successfully | Items: $cartItems")
                    },
                    onFailure = { exception ->
                        _uiState.value = UiState.Error(exception.message ?: "Unknown error occurred")
                        Log.e("CartViewModel", "Error fetching cart items: $exception")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    // Function to refresh the cart items
    fun refreshCartItems() {
        getCartItems()
    }

}
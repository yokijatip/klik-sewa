package com.sinergi5.kliksewa.ui.cart

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.sinergi5.kliksewa.model.CartItem
import com.sinergi5.kliksewa.repository.Repository
import com.sinergi5.kliksewa.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CartViewModel(context: Context) : ViewModel() {

    private val repository = Repository(context)

    private val _cartItems = MutableStateFlow<Resource<List<CartItem>>>(Resource.Loading())
    val cartItems = _cartItems.asStateFlow()

    private val _updateCartStatus = MutableStateFlow<Resource<Boolean>>(Resource.Success(false))
    val updateCartStatus = _updateCartStatus.asStateFlow()

    init {
        loadCartItems()
    }

    private fun loadCartItems() {
        viewModelScope.launch {
            try {
                // Get current user
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                    ?: throw Exception("User tidak terautentikasi")

                // Get user document ID from email
                val userEmail = FirebaseAuth.getInstance().currentUser?.email
                    ?: throw Exception("Email user tidak ditemukan")

                when (val userQuery = repository.getUserByEmail(userEmail)) {
                    is Resource.Success -> {
                        userQuery.data?.let { userId ->
                            _cartItems.value = repository.getCartItems(userId)
                        }
                    }

                    is Resource.Error -> {
                        _cartItems.value =
                            Resource.Error(userQuery.message ?: "Error loading user data")
                    }

                    is Resource.Loading -> {
                        _cartItems.value = Resource.Loading()
                    }
                }
            } catch (e: Exception) {
                _cartItems.value = Resource.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }

    fun updateCartItemQuantity(cartItemId: String, newQuantity: Int) {
        viewModelScope.launch {
            try {
                _updateCartStatus.value = Resource.Loading()
                val result = repository.updateCartItemQuantity(cartItemId, newQuantity)
                _updateCartStatus.value = result

                // Reload cart items after successful update
                if (result is Resource.Success) {
                    loadCartItems()
                }
            } catch (e: Exception) {
                _updateCartStatus.value = Resource.Error(e.message ?: "Gagal mengupdate quantity")
            }
        }
    }

    fun removeCartItem(cartItemId: String) {
        viewModelScope.launch {
            try {
                _updateCartStatus.value = Resource.Loading()
                val result = repository.removeFromCart(cartItemId)
                _updateCartStatus.value = result

                // Reload cart items after successful removal
                if (result is Resource.Success) {
                    loadCartItems()
                }
            } catch (e: Exception) {
                _updateCartStatus.value = Resource.Error(e.message ?: "Gagal menghapus item")
            }
        }
    }

    // Additional function to calculate totals
    fun calculateTotals(cartItems: List<CartItem>): Triple<Long, Long, Long> {
        val subtotal = cartItems.sumOf { it.totalPrice }
        val deliveryFee = 0L // Atau implementasi sesuai logic delivery fee
        val total = subtotal + deliveryFee

        return Triple(subtotal, deliveryFee, total)
    }
}
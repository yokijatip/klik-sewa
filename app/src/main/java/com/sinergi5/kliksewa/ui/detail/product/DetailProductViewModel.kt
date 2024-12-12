package com.sinergi5.kliksewa.ui.detail.product

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.sinergi5.kliksewa.model.Product
import com.sinergi5.kliksewa.repository.Repository
import com.sinergi5.kliksewa.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailProductViewModel(context: Context): ViewModel() {
    private val repository = Repository(context)

    private val _product = MutableStateFlow<Resource<Product>>(Resource.Loading())
    val product: StateFlow<Resource<Product>> = _product.asStateFlow()

    private val _favoriteStatus = MutableStateFlow<Resource<Boolean>>(Resource.Loading())
    val favoriteStatus = _favoriteStatus.asStateFlow()

    private val _isProductFavorited = MutableStateFlow(false)
    val isProductFavorited = _isProductFavorited.asStateFlow()

    private val _addToCartStatus = MutableStateFlow<Resource<Boolean>>(Resource.Loading())
    val addToCartStatus = _addToCartStatus.asStateFlow()

    fun addToCart(productId: String, productName: String, quantity: Int, price: Long) {
        viewModelScope.launch {
            _addToCartStatus.value = Resource.Loading()
            _addToCartStatus.value = repository.addToCart(productId, productName, quantity, price)
        }
    }

    fun getProductDetail(productId: String) {
        viewModelScope.launch {
            _product.value = Resource.Loading()
            _product.value = repository.getProductDetail(productId)
        }
    }


    fun toggleFavorite(productId: String) {
        viewModelScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null) {
                    _favoriteStatus.value = Resource.Loading()
                    val result = repository.toggleFavorite(productId, userId)
                    _favoriteStatus.value = result

                    // Update isProductFavorited state
                    if (result is Resource.Success && result.data == true) {
                        _isProductFavorited.value = !_isProductFavorited.value
                    }
                } else {
                    _favoriteStatus.value = Resource.Error("Silakan login terlebih dahulu")
                }
            } catch (e: Exception) {
                _favoriteStatus.value = Resource.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }

    fun setInitialFavoriteState(isFavorited: Boolean) {
        _isProductFavorited.value = isFavorited
    }

}
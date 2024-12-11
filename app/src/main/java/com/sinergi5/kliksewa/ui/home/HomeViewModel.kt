package com.sinergi5.kliksewa.ui.home

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

class HomeViewModel(context: Context) : ViewModel() {
    private val repository = Repository(context)
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    private val _products = MutableStateFlow<Resource<List<Product>>>(Resource.Loading())
    val products: StateFlow<Resource<List<Product>>> = _products.asStateFlow()

    private val _favoriteStatus = MutableStateFlow<Resource<Boolean>?>(null)
    val favoriteStatus: StateFlow<Resource<Boolean>?> = _favoriteStatus.asStateFlow()

    init {
        getRandomProduct()
    }

    private fun getRandomProduct() {
        viewModelScope.launch {
            _products.value = Resource.Loading()
            _products.value = repository.getRandomProduct()
        }
    }

    fun toggleFavorite(productId: String) {
        viewModelScope.launch {
            currentUserId?.let { userId ->
                _favoriteStatus.value = Resource.Loading()
                _favoriteStatus.value = repository.toggleFavorite(productId, userId)
            }
        }
    }

}
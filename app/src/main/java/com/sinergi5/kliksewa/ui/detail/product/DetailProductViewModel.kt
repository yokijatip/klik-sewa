package com.sinergi5.kliksewa.ui.detail.product

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun getProductDetail(productId: String) {
        viewModelScope.launch {
            _product.value = Resource.Loading()
            _product.value = repository.getProductDetail(productId)
        }
    }

}
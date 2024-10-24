package com.sinergi5.kliksewa.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinergi5.kliksewa.data.model.Item
import com.sinergi5.kliksewa.repository.Repository
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: Repository) : ViewModel() {
    private val _itemDetail = MutableLiveData<Result<Item>>()
    val itemDetail: LiveData<Result<Item>> get() = _itemDetail

    private val _addItemToCart = MutableLiveData<Result<Unit>>()
    val addItemToCart: LiveData<Result<Unit>> get() = _addItemToCart

    fun addItemToCart(itemId: String, quantity: Int = 1) {
        viewModelScope.launch {
            repository.addToCart(itemId, quantity)
        }
    }

    fun fetchItemDetails(itemId: String) {
        viewModelScope.launch {
            val result = repository.getDetailItem(itemId)
            _itemDetail.postValue(result)
        }
    }
}
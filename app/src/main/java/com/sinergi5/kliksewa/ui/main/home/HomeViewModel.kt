package com.sinergi5.kliksewa.ui.main.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinergi5.kliksewa.data.model.Item
import com.sinergi5.kliksewa.repository.Repository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: Repository) : ViewModel() {

    private val _items = MutableLiveData<List<Item>>()
    val items: LiveData<List<Item>> get() = _items

    init {
        getRandomItems()
    }

    @SuppressLint("NullSafeMutableLiveData")
    private fun getRandomItems() {
        viewModelScope.launch {
            val result = repository.getRandomRecommendation()
            if (result.isSuccess) {
                _items.postValue(result.getOrNull())
                Log.d("HomeViewModel", "Random items fetched successfully | Items : ${result.getOrNull()}")
            } else {
                Log.e("HomeViewModel", "Error fetching random items: ${result.exceptionOrNull()}")
            }
        }
    }

}
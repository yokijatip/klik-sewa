package com.sinergi5.kliksewa.ui.main.explore

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinergi5.kliksewa.data.model.CategoryItem
import com.sinergi5.kliksewa.data.model.Item
import com.sinergi5.kliksewa.repository.Repository
import kotlinx.coroutines.launch

class ExploreViewModel(private val repository: Repository) : ViewModel() {
    private val _categories = MutableLiveData<List<CategoryItem>>()
    val categories: LiveData<List<CategoryItem>> get() = _categories

    private val _itemByCategory = MutableLiveData<List<Item>>()
    val itemByCategory: LiveData<List<Item>> get() = _itemByCategory

    init {
        fetchCategories()
        fetchItemByCategories()
    }

    @SuppressLint("NullSafeMutableLiveData")
    private fun fetchCategories() {
        viewModelScope.launch {
            val result = repository.getCategoryItem()
            if (result.isSuccess) {
                _categories.postValue(result.getOrNull())
            } else {
                Log.e("ExploreViewModel", "Error Fetching Categories : ${result.exceptionOrNull()}")
            }
        }
    }

    @SuppressLint("NullSafeMutableLiveData")
    private fun fetchItemByCategories(categoryId: String? = null) {
        viewModelScope.launch {
            val result = repository.getItemsByCategoryId(categoryId)
            if (result.isSuccess) {
                _itemByCategory.postValue(result.getOrNull())
            } else {
                Log.e("ExploreViewModel", "Error Fetching Items : ${result.exceptionOrNull()}")
            }
        }
    }


}
package com.sinergi5.kliksewa.ui.main.explore

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinergi5.kliksewa.data.model.CategoryItem
import com.sinergi5.kliksewa.data.model.Item
import com.sinergi5.kliksewa.helper.UiState
import com.sinergi5.kliksewa.repository.Repository
import kotlinx.coroutines.launch

class ExploreViewModel(private val repository: Repository) : ViewModel() {
    private val _categories = MutableLiveData<List<CategoryItem>>()
    val categories: LiveData<List<CategoryItem>> get() = _categories

    private val _uiState = MutableLiveData<UiState<List<Item>>>()
    val uiState: LiveData<UiState<List<Item>>> get() = _uiState

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

    @JvmOverloads
    fun fetchItemByCategories(categoryId: String? = null) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val result = repository.getItemsByCategoryId(categoryId)
                result.fold(
                    onSuccess = {
                        _uiState.value = UiState.Success(it)
                        Log.d("EploreViewModel", "Success Fetching Item By Categories | Items: $it")
                    },
                    onFailure = {
                        _uiState.value = UiState.Error(it.message ?: "Unknown Error Occured")
                        Log.e(
                            "ExploreViewModel",
                            "Error Fetching Item By Categories : ${it.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown Error Occured")
            }
        }
    }

    fun refresh() {
        fetchItemByCategories()
    }


}
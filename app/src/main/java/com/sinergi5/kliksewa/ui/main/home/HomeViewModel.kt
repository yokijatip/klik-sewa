package com.sinergi5.kliksewa.ui.main.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinergi5.kliksewa.data.model.Item
import com.sinergi5.kliksewa.helper.UiState
import com.sinergi5.kliksewa.repository.Repository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: Repository) : ViewModel() {

    private val _uiState = MutableLiveData<UiState<List<Item>>>()
    val uiState: LiveData<UiState<List<Item>>> = _uiState

    init {
        getRandomItems()
    }

    private fun getRandomItems() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val result = repository.getRandomRecommendation()
                result.fold(
                    onSuccess = { items ->
                        _uiState.value = UiState.Success(items)
                        Log.d("HomeViewModel", "Random items fetched successfully | Items : $items")
                    },
                    onFailure = { exception ->
                        _uiState.value =
                            UiState.Error(exception.message ?: "Unknown error occurred")
                        Log.e("HomeViewModel", "Error fetching random items: $exception")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun refreshItems() {
        getRandomItems()
    }

}
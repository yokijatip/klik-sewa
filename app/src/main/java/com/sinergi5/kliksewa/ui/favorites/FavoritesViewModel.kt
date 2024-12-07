package com.sinergi5.kliksewa.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoritesViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is explore Fragment"
    }

    val text: LiveData<String> = _text
}
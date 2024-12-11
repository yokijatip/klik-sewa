package com.sinergi5.kliksewa.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sinergi5.kliksewa.ui.auth.ui.auth.AuthViewModel
import com.sinergi5.kliksewa.ui.detail.product.DetailProductViewModel
import com.sinergi5.kliksewa.ui.home.HomeViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(context) as T
        } else if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(context) as T
        } else if (modelClass.isAssignableFrom(DetailProductViewModel::class.java)) {
            return DetailProductViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
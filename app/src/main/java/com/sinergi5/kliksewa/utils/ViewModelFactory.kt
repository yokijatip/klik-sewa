package com.sinergi5.kliksewa.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sinergi5.kliksewa.repository.Repository
import com.sinergi5.kliksewa.ui.auth.AuthViewModel
import com.sinergi5.kliksewa.ui.cart.CartViewModel
import com.sinergi5.kliksewa.ui.detail.DetailViewModel
import com.sinergi5.kliksewa.ui.main.explore.ExploreViewModel
import com.sinergi5.kliksewa.ui.main.home.HomeViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(ExploreViewModel::class.java)) {
            return ExploreViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return CartViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
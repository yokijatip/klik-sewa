package com.sinergi5.kliksewa.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sinergi5.kliksewa.repository.Repository
import com.sinergi5.kliksewa.ui.auth.AuthViewModel

class ViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
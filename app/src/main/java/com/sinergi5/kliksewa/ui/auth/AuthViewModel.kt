package com.sinergi5.kliksewa.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinergi5.kliksewa.repository.Repository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: Repository) : ViewModel() {
    private val _loginResult = MutableLiveData<Result<Unit>>()
    val loginResult: LiveData<Result<Unit>> get() = _loginResult

    private val _registerResult = MutableLiveData<Result<Unit>>()
    val registerResult: LiveData<Result<Unit>> get() = _registerResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = repository.login(email, password)
            _loginResult.postValue(result)
        }
    }

    fun register(email: String, password: String, name: String, phone: String) {
        viewModelScope.launch {
            val result = repository.register(email, password, name, phone)
            _registerResult.postValue(result)
        }
    }
}
package com.sinergi5.kliksewa.ui.auth.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinergi5.kliksewa.repository.Repository
import com.sinergi5.kliksewa.utils.Resource
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: Repository) : ViewModel() {
    private val _loginResult = MutableLiveData<Resource<Boolean>>()
    val loginResult: LiveData<Resource<Boolean>> get() = _loginResult

    private val _registerResult = MutableLiveData<Resource<Boolean>>()
    val registerResult: LiveData<Resource<Boolean>> get() = _registerResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.postValue(Resource.Loading())
            val result = repository.login(email, password)
            _loginResult.postValue(result)
        }
    }

    fun register(fullname: String, phoneNumber: String, email: String, password: String) {
        viewModelScope.launch {
            _registerResult.postValue(Resource.Loading())
            val result = repository.register(fullname, phoneNumber, email, password)
            _registerResult.postValue(result)
        }
    }
}
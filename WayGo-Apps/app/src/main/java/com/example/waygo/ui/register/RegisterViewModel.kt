package com.example.waygo.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.waygo.data.Storage
import kotlinx.coroutines.launch

class RegisterViewModel(private val storage: Storage) : ViewModel() {

    fun registerUser(
        username: String,
        email: String,
        password: String,

    ) = storage.registerUser(username, email, password)

}
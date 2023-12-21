package com.example.waygo.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.waygo.data.Storage
import com.example.waygo.data.pref.UserModel
import kotlinx.coroutines.launch

class LoginViewModel(
    private val storage: Storage
) : ViewModel() {

    fun setLogin(email : String, password : String) = storage.LoginUser(email, password)

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            storage.saveSession(user)
        }
    }
}
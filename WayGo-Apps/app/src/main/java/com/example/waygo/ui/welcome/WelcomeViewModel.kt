package com.example.waygo.ui.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.waygo.data.Storage
import com.example.waygo.data.pref.UserModel
import kotlinx.coroutines.launch

class WelcomeViewModel(val storage: Storage): ViewModel() {

    fun login(email: String, password: String) = storage.LoginUser(email, password)

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            storage.saveSession(user)
        }
    }
}
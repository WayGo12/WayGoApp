package com.example.waygo.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.waygo.data.Storage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileViewModel (private val storage: Storage): ViewModel() {

    fun getUser(id:String) = storage.getDetailUser(id)

    fun logout() {
        viewModelScope.launch {
            storage.logout()
        }
    }
    suspend fun getSession() {
        storage.getSession().first()
    }
}
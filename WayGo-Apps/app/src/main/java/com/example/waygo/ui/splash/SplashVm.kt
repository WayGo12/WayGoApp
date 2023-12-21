package com.example.waygo.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.waygo.data.Storage
import com.example.waygo.data.pref.UserModel

class SplashVm (private val storage: Storage): ViewModel() {

    fun getUser(): LiveData<UserModel> {
        return storage.getSession().asLiveData()
    }
}
package com.example.waygo.helper

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.waygo.ui.home.HomeViewModel
import com.example.waygo.ui.login.LoginViewModel
import com.example.waygo.ui.profile.ProfileViewModel
import com.example.waygo.ui.register.RegisterViewModel
import com.example.waygo.ui.welcome.WelcomeViewModel
import com.example.waygo.Injection
import com.example.waygo.data.Repository
import com.example.waygo.ui.detail.DetailViewModel
import com.example.waygo.ui.splash.SplashVm
import com.example.waygo.ui.customRundown.CustomRundownVM
import com.example.waygo.ui.rundown.GenerateRundownVM

class VMFactory  private constructor(private val repository: Repository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) ->{
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SplashVm::class.java) -> {
                SplashVm(repository) as T
            }
            modelClass.isAssignableFrom(WelcomeViewModel::class.java) -> {
                WelcomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(repository) as T
            }
            modelClass.isAssignableFrom(CustomRundownVM::class.java) -> {
                CustomRundownVM(repository) as T
            }
            modelClass.isAssignableFrom(GenerateRundownVM::class.java) -> {
                GenerateRundownVM(repository) as T
            }
            else ->    throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
    companion object {
        @Volatile
        private var instance: VMFactory? = null
        fun getInstance(context: Context): VMFactory =
            instance ?: synchronized(this) {
                instance ?: VMFactory(Injection.provideRepository(context))
            }.also { instance = it }

        fun clearInstance(){
            Repository.clearInstance()
            instance = null
        }
    }
}
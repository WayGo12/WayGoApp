package com.example.waygo

import android.content.Context
import com.example.waygo.data.Storage
import com.example.waygo.data.pref.UserPrefs
import com.example.waygo.data.pref.dataStore
import com.example.waygo.data.retrofit.ApiConfig
import com.example.waygo.data.retrofit.MlApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): Storage {
        val pref = UserPrefs.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.accessToken)
        val mlService = MlApi.getMLService()
        return Storage.getInstance(apiService,mlService, pref)
    }
}
package com.example.waygo.ui.customRundown

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.waygo.data.Storage
import kotlinx.coroutines.flow.first

class CustomRundownVM(private val storage: Storage) : ViewModel() {

    fun getAllRundown() = storage.getAllRundown().cachedIn(viewModelScope)

    suspend fun getSession() {
        storage.getSession().first()
    }
}
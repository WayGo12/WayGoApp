package com.example.waygo.ui.customRundown

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.waygo.data.Repository
import kotlinx.coroutines.flow.first

class CustomRundownVM(private val repository: Repository) : ViewModel() {

    fun getAllRundown() = repository.getAllRundown().cachedIn(viewModelScope)

    suspend fun getSession() {
        repository.getSession().first()
    }
}
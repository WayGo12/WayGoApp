package com.example.waygo.ui.rundown

import androidx.lifecycle.ViewModel
import com.example.waygo.data.Repository
import com.example.waygo.data.retrofit.ApiService
import kotlinx.coroutines.flow.first
import retrofit2.Call
import retrofit2.Response

class GenerateRundownVM(private val repository: Repository) : ViewModel() {


    fun rundownUser(
        user_id: String,
        region: String,

        ) = repository.generateRundownUser(user_id, region)

}
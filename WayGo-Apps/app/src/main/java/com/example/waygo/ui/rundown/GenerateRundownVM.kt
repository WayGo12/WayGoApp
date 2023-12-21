package com.example.waygo.ui.rundown

import androidx.lifecycle.ViewModel
import com.example.waygo.data.Storage


class GenerateRundownVM(private val storage: Storage) : ViewModel() {


    fun rundownUser(
        user_id: String,
        region: String,

        ) = storage.generateRundownUser(user_id, region)

}
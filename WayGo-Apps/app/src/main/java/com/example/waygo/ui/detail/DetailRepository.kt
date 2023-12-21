package com.example.waygo.ui.detail

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.waygo.data.response.VocationDao
import com.example.waygo.data.response.VocationEntity
import com.example.waygo.data.response.VocationRoom
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DetailRepository(application: Application) {
    private val userDao: VocationDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val database = VocationRoom.getDatabase(application)
        userDao = database.userDao()
    }

    fun getAllWishlistData(): LiveData<List<VocationEntity>> = userDao.getAllWishlistData()

    fun insert(vocation: VocationEntity) {
        executorService.execute {
            userDao.insert((vocation))
        }
    }

    fun delete(vocation: VocationEntity) {
        executorService.execute {
            userDao.delete(vocation)
        }
    }

    fun getDataByUsername(name: String): LiveData<List<VocationEntity>> = userDao.getDataByname(name)
}

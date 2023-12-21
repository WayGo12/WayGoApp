package com.example.waygo.ui.detail

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.waygo.data.Repository
import com.example.waygo.data.response.AllTouristSpotsItem
import com.example.waygo.data.response.VocationEntity

class DetailViewModel(private val repository: Repository, private val application: Application) : ViewModel() {

    private val _detailUser = MutableLiveData<AllTouristSpotsItem>()
    val detailUser: LiveData<AllTouristSpotsItem> = _detailUser
    private val detailUserData: DetailRepository = DetailRepository(application)

    fun getTouristById(id: String) = repository.getDetailTourism(id)
    fun getDataByUsername(name: String) = detailUserData.getDataByUsername(name)

    fun insert(vocation: VocationEntity) {
        detailUserData.insert(vocation)
    }

    fun delete(vocation: VocationEntity) {
        detailUserData.delete(vocation)
    }
}

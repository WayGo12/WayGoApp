package com.example.waygo.ui.wishlist

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.waygo.ui.detail.DetailRepository

class WishlistViewModel (private val application: Application) : ViewModel() {
    private val detailVocationData: DetailRepository = DetailRepository(application)
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getAllWishlistData() = detailVocationData.getAllWishlistData()

    class ViewModelFactory(private val application: Application) : ViewModelProvider.NewInstanceFactory() {
        companion object {
            @Volatile
            private var INSTANCE: ViewModelFactory? = null

            @JvmStatic
            fun getInstance(application: Application): ViewModelFactory {
                if (INSTANCE == null) {
                    synchronized(ViewModelFactory::class.java) {
                        INSTANCE = ViewModelFactory(application)
                    }
                }
                return INSTANCE as ViewModelFactory
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WishlistViewModel::class.java)) {
                return WishlistViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
        }
    }
}
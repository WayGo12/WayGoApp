package com.example.waygo.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.waygo.data.paging.RundownPaging
import com.example.waygo.data.paging.TouristPaging
import com.example.waygo.data.pref.UserModel
import com.example.waygo.data.pref.UserPrefs
import com.example.waygo.data.response.AllTouristSpotsItem
import com.example.waygo.data.response.ErrorResponse
import com.example.waygo.data.response.RundownItem
import com.example.waygo.data.retrofit.ApiService
import com.example.waygo.data.retrofit.MlService
import com.example.waygo.helper.Result
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

class Repository  public constructor(
    private val apiService: ApiService,
    private val userPrefs: UserPrefs,
    private val mlService: MlService,
){

    suspend fun saveSession(user: UserModel) {
        userPrefs.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPrefs.getSession()
    }

    suspend fun logout() {
        userPrefs.logout()
    }

    fun registerUser(
        name: String,
        email: String,
        password: String

    ) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(name, email, password)
            if (response.isSuccessful) {
                emit(Result.Success(response.body()!!))
            } else {
                val errorResponse = Gson().fromJson(response.errorBody()?.string(), ErrorResponse::class.java)
                emit(Result.Error(errorResponse.message.toString()))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun setLogin(email : String, password: String) = liveData{
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            if (response.isSuccessful) {
                emit(Result.Success(response.body()!!))
            } else {
                val errorResponse = Gson().fromJson(response.errorBody()?.string(), ErrorResponse::class.java)
                emit(Result.Error(errorResponse.message.toString()))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getAllTourist(): LiveData<PagingData<AllTouristSpotsItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                TouristPaging(apiService)
            }
        ).liveData
    }

    fun getDetailTourism(id: String) = liveData{
        emit(Result.Loading)
        try {
            val response = apiService.getDetailTourist(id)
            if (response.isSuccessful) {
                emit(Result.Success(response.body()!!))
            } else {
                val errorResponse = Gson().fromJson(response.errorBody()?.string(), ErrorResponse::class.java)
                emit(Result.Error(errorResponse.message.toString()))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getDetailUser(id: String) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getDetailUser(id)
            if (response.isSuccessful) {
                emit(Result.Success(response.body()!!))
            } else {
                val errorResponse = Gson().fromJson(response.errorBody()?.string(), ErrorResponse::class.java)
                emit(Result.Error(errorResponse.message.toString()))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun generateRundownUser(
        user_id: String,
        region: String

    ) = liveData {
        emit(Result.Loading)
        try {
            val response = mlService.generate(user_id, region)
            if (response.isSuccessful) {
                emit(Result.Success(response.body()!!))
            } else {
                val errorResponse = Gson().fromJson(response.errorBody()?.string(), ErrorResponse::class.java)
                emit(Result.Error(errorResponse.message.toString()))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }


    fun getAllRundown(): LiveData<PagingData<RundownItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                RundownPaging(mlService)
            }
        ).liveData
    }


    companion object{
        private const val TAG = "Repository"

        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            apiService: ApiService,
            mlService: MlService,
            userPreference: UserPrefs,
        ): Repository =
            instance ?: synchronized(this){
                instance ?: Repository(apiService,userPreference,mlService)
            }.also { instance = it }

        fun clearInstance(){
            instance = null
        }
    }
}
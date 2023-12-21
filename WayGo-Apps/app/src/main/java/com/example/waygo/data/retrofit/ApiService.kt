package com.example.waygo.data.retrofit

import com.example.waygo.data.response.DetailTouristResponse
import com.example.waygo.data.response.DetailUserResponse
import com.example.waygo.data.response.LoginResponse
import com.example.waygo.data.response.RegisterResponse
import com.example.waygo.data.response.TouristResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("users/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST("users/register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): Response<RegisterResponse>

    @GET("users/{id}")
    suspend fun getDetailUser(
        @Path("id") id: String
    ): Response<DetailUserResponse>

    @GET("touristspot")
    suspend fun getAllTourist(
        @Query("page") page: Int,
        @Query("pageSize") size: Int
    ): TouristResponse

    @GET("touristspot/detail/{id}")
    suspend fun getDetailTourist(
        @Path("id") id: String
    ): Response<DetailTouristResponse>


}
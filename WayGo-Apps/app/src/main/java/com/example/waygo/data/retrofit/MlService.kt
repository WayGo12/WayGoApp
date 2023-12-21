package com.example.waygo.data.retrofit

import com.example.waygo.data.response.ResultRundownResponse
import com.example.waygo.data.response.RundownResponse
import com.example.waygo.data.response.TouristResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MlService {
    @FormUrlEncoded
    @POST("generate")
    suspend fun generate(
        @Field("user_id") user_id: String,
        @Field("region") region: String,
    ): Response<RundownResponse>


    @GET("generate?user_id=1&region=North%20Bali")
    suspend fun getRundown(
        @Query("page") page: Int,
        @Query("pageSize") size: Int
    ): ResultRundownResponse
}
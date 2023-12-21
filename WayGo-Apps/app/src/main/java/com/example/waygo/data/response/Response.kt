package com.example.waygo.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class LoginResponse(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("accessToken")
    val accessToken: String
) : Parcelable

@Parcelize
data class RegisterResponse(

    @field:SerializedName("data")
    val data: Data,

    @field:SerializedName("message")
    val message: String
) : Parcelable

@Parcelize
data class Data(

    @field:SerializedName("password")
    val password: String,

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("name")
    val name: String,

    ) : Parcelable


@Parcelize
data class DetailUserResponse(

    @field:SerializedName("user")
    val user: User
) : Parcelable

@Parcelize
data class User(

    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:SerializedName("password")
    val password: String,

    @field:SerializedName("role")
    val role: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("updatedAt")
    val updatedAt: String
):Parcelable

@Parcelize
data class ErrorResponse (
    @field:SerializedName("error")
    val error: Boolean? = null,
    @field:SerializedName("message")
    val message: String? = null
) : Parcelable





@Parcelize
data class TouristResponse(

    @field:SerializedName("allTouristSpots")
    val allTouristSpots: List<AllTouristSpotsItem>
) : Parcelable

@Parcelize
data class AllTouristSpotsItem(

    @field:SerializedName("coordinat")
    val coordinat: String,

    @field:SerializedName("locate_url")
    val locateUrl: String,

    @field:SerializedName("image_url")
    val imageUrl: String,

    @field:SerializedName("estimate_price")
    val estimatePrice: String,

    @field:SerializedName("rating")
    val rating: String,

    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("opening_hours")
    val openingHours: String,

    @field:SerializedName("location")
    val location: String,

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("region")
    val region: String,

    @field:SerializedName("category")
    val category: String,

    @field:SerializedName("updatedAt")
    val updatedAt: String
) : Parcelable




@Parcelize
data class DetailTouristResponse(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("touristSpot")
    val touristSpot: TouristSpot
) : Parcelable

@Parcelize
data class TouristSpot(

    @field:SerializedName("coordinat")
    val coordinat: String,

    @field:SerializedName("locate_url")
    val locateUrl: String,

    @field:SerializedName("image_url")
    val imageUrl: String,

    @field:SerializedName("estimate_price")
    val estimatePrice: String,

    @field:SerializedName("rating")
    val rating: Float,

    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("opening_hours")
    val openingHours: String,

    @field:SerializedName("location")
    val location: String,

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("region")
    val region: String,

    @field:SerializedName("category")
    val category: String,

    @field:SerializedName("updatedAt")
    val updatedAt: String
) : Parcelable



@Parcelize
data class RundownResponse(

    @field:SerializedName("status")
    val status: String

) : Parcelable




@Parcelize
data class ResultRundownResponse(

    @field:SerializedName("rundown")
    val rundown: @RawValue List<RundownItem>,

    @field:SerializedName("recommendations")
    val recommendations: Recommendations,

    @field:SerializedName("status")
    val status: String
) : Parcelable

@Parcelize
data class Recommendations(

    @field:SerializedName("Kategori")
    val kategori: List<String>,

    @field:SerializedName("Nama Tempat")
    val namaTempat: List<String>,

    @field:SerializedName("Latitude")
    val latitude: List<String>,

    @field:SerializedName("Longitude")
    val longitude: List<String>
) : Parcelable

@Parcelize
data class RundownItem(

    @field:SerializedName("Jarak Tempat")
    val jarakTempat: String,

    @field:SerializedName("Kategori")
    val kategori: String,

    @field:SerializedName("Jam Rundown")
    val jamRundown: String,

    @field:SerializedName("Nama Tempat")
    val namaTempat: String
) : Parcelable



package com.example.waygo.data.response

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface VocationDao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(vocation: VocationEntity)

    @Delete
    fun delete(vocation: VocationEntity)

    @Query("SELECT * FROM Vocation ORDER BY name ASC")
    fun getAllWishlistData(): LiveData<List<VocationEntity>>

    @Query("SELECT * FROM Vocation WHERE name = :name")
    fun getDataByname(name: String): LiveData<List<VocationEntity>>
}
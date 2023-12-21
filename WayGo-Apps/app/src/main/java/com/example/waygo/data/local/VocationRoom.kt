package com.example.waygo.data.response

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [VocationEntity::class], version =1)
abstract class VocationRoom: RoomDatabase() {
    abstract fun userDao(): VocationDao

    companion object {
        @Volatile
        private var INSTANCE: VocationRoom? = null

        @JvmStatic
        fun getDatabase(context: Context): VocationRoom {
            if (INSTANCE == null) {
                synchronized(VocationRoom::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        VocationRoom::class.java, "vocation_db"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    Log.d("VocationRoom", "Database initialized")
                }

            }
            return INSTANCE as VocationRoom
        }
    }
}
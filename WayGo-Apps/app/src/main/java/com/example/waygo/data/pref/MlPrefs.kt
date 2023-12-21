package com.example.waygo.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.mlDataStore: DataStore<Preferences> by preferencesDataStore(name = "rundown")
class MlPrefs private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveRundown(mlModel: MlModel) {
        dataStore.edit { preferences ->
            preferences[MlPrefs.PLACENAME_KEY] = mlModel.namaTempat
            preferences[MlPrefs.JAMRUNDOWN_KEY] = mlModel.jamRundown
        }
    }

    fun getMl(): Flow<MlModel> {
        return dataStore.data.map { preferences ->
            MlModel(
                preferences[MlPrefs.PLACENAME_KEY] ?: "",
                preferences[MlPrefs.JAMRUNDOWN_KEY] ?: ""
            )
        }
    }

    suspend fun delete() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: MlPrefs? = null

        private val PLACENAME_KEY = stringPreferencesKey("namaTempat")
        private val JAMRUNDOWN_KEY = stringPreferencesKey("jamRundown")


        fun getInstance(dataStore: DataStore<Preferences>): MlPrefs {
            return INSTANCE ?: synchronized(this) {
                val instance = MlPrefs(dataStore)
                INSTANCE = instance
                instance
            }

        }

    }
}

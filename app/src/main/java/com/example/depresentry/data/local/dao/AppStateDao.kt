package com.example.depresentry.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.depresentry.data.local.entity.AppStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppStateDao {
    @Query("SELECT * FROM app_state WHERE id = 1")
    fun getAppState(): Flow<AppStateEntity?>

    @Query("INSERT OR REPLACE INTO app_state (id, isOnboardingCompleted) VALUES (1, :completed)")
    suspend fun updateAppState(completed: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAppState(appState: AppStateEntity)

    // İlk kullanımda default değeri set etmek için
    @Query("INSERT OR IGNORE INTO app_state (id, isOnboardingCompleted) VALUES (1, 0)")
    suspend fun initializeAppState()
}
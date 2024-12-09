package com.example.depresentry.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_state")
data class AppStateEntity(
    @PrimaryKey
    val id: Int = 1,
    val isOnboardingCompleted: Boolean = false
) {
    companion object {
        fun default() = AppStateEntity(id = 1, isOnboardingCompleted = false)
    }
}
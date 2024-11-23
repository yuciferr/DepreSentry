package com.example.depresentry.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile_images")
data class ProfileImageEntity(
    @PrimaryKey
    val userId: String,
    val imagePath: String,
    val updatedAt: Long = System.currentTimeMillis()
) 
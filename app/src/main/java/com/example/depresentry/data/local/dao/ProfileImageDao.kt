package com.example.depresentry.data.local.dao

import androidx.room.*
import com.example.depresentry.data.local.entity.ProfileImageEntity

@Dao
interface ProfileImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfileImage(profileImage: ProfileImageEntity)

    @Query("SELECT * FROM profile_images WHERE userId = :userId")
    suspend fun getProfileImage(userId: String): ProfileImageEntity?

    @Query("DELETE FROM profile_images WHERE userId = :userId")
    suspend fun deleteProfileImage(userId: String)
} 
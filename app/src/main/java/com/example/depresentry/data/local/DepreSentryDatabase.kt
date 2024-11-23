package com.example.depresentry.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.depresentry.data.local.dao.ProfileImageDao
import com.example.depresentry.data.local.entity.ProfileImageEntity

@Database(
    entities = [ProfileImageEntity::class],
    version = 1
)
abstract class DepreSentryDatabase : RoomDatabase() {
    abstract val profileImageDao: ProfileImageDao
} 
package com.example.depresentry.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.depresentry.data.local.dao.ProfileImageDao
import com.example.depresentry.data.local.entity.ProfileImageEntity
import com.example.depresentry.data.local.entity.ChatMessageEntity
import com.example.depresentry.data.local.dao.ChatMessageDao

@Database(
    entities = [
        ProfileImageEntity::class,
        ChatMessageEntity::class
    ],
    version = 2
)
abstract class DepreSentryDatabase : RoomDatabase() {
    abstract val profileImageDao: ProfileImageDao
    abstract fun chatMessageDao(): ChatMessageDao
} 
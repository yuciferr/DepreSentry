package com.example.depresentry.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.depresentry.data.local.dao.ProfileImageDao
import com.example.depresentry.data.local.dao.ChatMessageDao
import com.example.depresentry.data.local.dao.AppStateDao
import com.example.depresentry.data.local.dao.DailyDataDao
import com.example.depresentry.data.local.entity.ProfileImageEntity
import com.example.depresentry.data.local.entity.ChatMessageEntity
import com.example.depresentry.data.local.entity.AppStateEntity
import com.example.depresentry.data.local.entity.DailyDataEntity
import com.example.depresentry.data.local.converter.Converters

@Database(
    entities = [
        ProfileImageEntity::class,
        ChatMessageEntity::class,
        AppStateEntity::class,
        DailyDataEntity::class
    ],
    version = 4
)
@TypeConverters(Converters::class)
abstract class DepreSentryDatabase : RoomDatabase() {
    abstract val profileImageDao: ProfileImageDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun appStateDao(): AppStateDao
    abstract fun dailyDataDao(): DailyDataDao
} 
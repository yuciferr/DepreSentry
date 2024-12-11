package com.example.depresentry.di

import android.content.Context
import androidx.room.Room
import com.example.depresentry.data.local.DepreSentryDatabase
import com.example.depresentry.data.local.dao.ChatMessageDao
import com.example.depresentry.data.local.dao.AppStateDao
import com.example.depresentry.data.local.dao.DailyDataDao
import com.example.depresentry.data.local.dao.ProfileImageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDepreSentryDatabase(
        @ApplicationContext context: Context
    ): DepreSentryDatabase {
        return Room.databaseBuilder(
            context,
            DepreSentryDatabase::class.java,
            "depresentry_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideProfileImageDao(database: DepreSentryDatabase): ProfileImageDao {
        return database.profileImageDao
    }

    @Provides
    @Singleton
    fun provideChatMessageDao(database: DepreSentryDatabase): ChatMessageDao {
        return database.chatMessageDao()
    }

    @Provides
    @Singleton
    fun provideAppStateDao(database: DepreSentryDatabase): AppStateDao {
        return database.appStateDao()
    }

    @Provides
    @Singleton
    fun provideDailyDataDao(database: DepreSentryDatabase): DailyDataDao {
        return database.dailyDataDao()
    }
} 
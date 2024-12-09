package com.example.depresentry.di

import android.content.Context
import androidx.room.Room
import com.example.depresentry.data.local.DepreSentryDatabase
import com.example.depresentry.data.local.dao.ChatMessageDao
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
        ).build()
    }

    @Provides
    @Singleton
    fun provideProfileImageDao(db: DepreSentryDatabase) = db.profileImageDao

    @Provides
    fun provideChatMessageDao(database: DepreSentryDatabase): ChatMessageDao {
        return database.chatMessageDao()
    }
} 
package com.example.depresentry.di

import UsageStatsRepositoryImpl
import android.content.Context
import com.example.depresentry.data.local.dao.ChatMessageDao
import com.example.depresentry.data.local.dao.ProfileImageDao
import com.example.depresentry.data.local.dao.DailyDataDao
import com.example.depresentry.data.remote.api.FirebaseAuthService
import com.example.depresentry.data.remote.api.FireStoreDatabaseService
import com.example.depresentry.data.remote.api.GeminiAIService
import com.example.depresentry.data.repository.UserRepositoryImpl
import com.example.depresentry.data.repository.UserDataRepositoryImpl
import com.example.depresentry.data.repository.GeminiRepositoryImpl
import com.example.depresentry.data.service.UsageStatsService
import com.example.depresentry.domain.repository.UserRepository
import com.example.depresentry.domain.repository.UserDataRepository
import com.example.depresentry.domain.repository.GeminiRepository
import com.example.depresentry.domain.repository.UsageStatsRepository
import com.example.depresentry.domain.usecase.auth.GetCurrentUserIdUseCase
import com.example.depresentry.domain.usecase.userData.local.ClearLocalChatHistoryUseCase
import com.example.depresentry.domain.usecase.userData.local.GetLocalChatHistoryUseCase
import com.example.depresentry.domain.usecase.userData.local.InsertLocalChatMessageUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(
        authService: FirebaseAuthService,
        databaseService: FireStoreDatabaseService,
        profileImageDao: ProfileImageDao
    ): UserRepository {
        return UserRepositoryImpl(
            authService = authService,
            databaseService = databaseService,
            profileImageDao = profileImageDao
        )
    }

    @Provides
    @Singleton
    fun provideUserDataRepository(
        databaseService: FireStoreDatabaseService,
        chatMessageDao: ChatMessageDao,
        dailyDataDao: DailyDataDao
    ): UserDataRepository {
        return UserDataRepositoryImpl(
            databaseService = databaseService,
            chatMessageDao = chatMessageDao,
            dailyDataDao = dailyDataDao
        )
    }

    @Provides
    @Singleton
    fun provideGeminiRepository(
        geminiAIService: GeminiAIService,
        chatMessageDao: ChatMessageDao,
        getCurrentUserIdUseCase: GetCurrentUserIdUseCase
    ): GeminiRepository {
        return GeminiRepositoryImpl(
            geminiService = geminiAIService,
            chatMessageDao = chatMessageDao,
            getCurrentUserIdUseCase = getCurrentUserIdUseCase
        )
    }

    @Provides
    @Singleton
    fun provideGeminiService(
        getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
        insertLocalChatMessageUseCase: InsertLocalChatMessageUseCase,
        getLocalChatHistoryUseCase: GetLocalChatHistoryUseCase,
        clearLocalChatHistoryUseCase: ClearLocalChatHistoryUseCase,
    ): GeminiAIService {
        return GeminiAIService(
            getCurrentUserIdUseCase = getCurrentUserIdUseCase,
            insertLocalChatMessageUseCase = insertLocalChatMessageUseCase,
            getLocalChatHistoryUseCase = getLocalChatHistoryUseCase,
            clearLocalChatHistoryUseCase = clearLocalChatHistoryUseCase
        )
    }

    @Provides
    @Singleton
    fun provideUsageStatsService(
        @ApplicationContext context: Context
    ): UsageStatsService {
        return UsageStatsService(context)
    }

    @Provides
    @Singleton
    fun provideUsageStatsRepository(
        usageStatsService: UsageStatsService
    ): UsageStatsRepository {
        return UsageStatsRepositoryImpl(usageStatsService)
    }
}

package com.example.depresentry.di

import com.example.depresentry.data.local.dao.ProfileImageDao
import com.example.depresentry.data.remote.api.FirebaseAuthService
import com.example.depresentry.data.remote.api.FireStoreDatabaseService
import com.example.depresentry.data.repository.UserRepositoryImpl
import com.example.depresentry.data.repository.UserDataRepositoryImpl
import com.example.depresentry.domain.repository.UserRepository
import com.example.depresentry.domain.repository.UserDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
        databaseService: FireStoreDatabaseService
    ): UserDataRepository {
        return UserDataRepositoryImpl(databaseService)
    }
}

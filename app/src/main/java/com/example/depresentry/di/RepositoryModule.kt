package com.example.depresentry.di

import com.example.depresentry.data.repository.UserRepositoryImpl
import com.example.depresentry.domain.repository.UserRepository
import com.example.depresentry.data.remote.api.FirebaseAuthService
import com.example.depresentry.data.remote.api.FireStoreDatabaseService
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
        databaseService: FireStoreDatabaseService
    ): UserRepository {
        return UserRepositoryImpl(authService, databaseService)
    }
}

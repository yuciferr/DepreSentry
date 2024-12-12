package com.example.depresentry.di

import com.example.depresentry.data.remote.api.FireStoreDatabaseService
import com.example.depresentry.data.remote.api.FirebaseAuthService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.google.gson.Gson

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseAuthService(auth: FirebaseAuth): FirebaseAuthService {
        return FirebaseAuthService(auth)
    }

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseDatabaseService(
        firestore: FirebaseFirestore
    ): FireStoreDatabaseService = FireStoreDatabaseService(firestore)

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()
}
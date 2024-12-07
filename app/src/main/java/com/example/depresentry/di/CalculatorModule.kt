package com.example.depresentry.di

import com.example.depresentry.domain.calculator.DepressionScoreCalculator
import com.example.depresentry.domain.calculator.DefaultDepressionScoreCalculator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CalculatorModule {
    
    @Provides
    @Singleton
    fun provideDepressionScoreCalculator(): DepressionScoreCalculator {
        return DefaultDepressionScoreCalculator()
    }
} 
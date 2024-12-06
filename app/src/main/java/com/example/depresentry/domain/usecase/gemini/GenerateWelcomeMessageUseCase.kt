package com.example.depresentry.domain.usecase.gemini

import com.example.depresentry.domain.repository.GeminiRepository
import javax.inject.Inject

class GenerateWelcomeMessageUseCase @Inject constructor(
    private val repository: GeminiRepository
) {
    suspend operator fun invoke(): Result<String> {
        return repository.generateWelcomeMessage()
    }
} 
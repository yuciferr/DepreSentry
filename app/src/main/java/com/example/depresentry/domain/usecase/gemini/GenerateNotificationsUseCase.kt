package com.example.depresentry.domain.usecase.gemini

import com.example.depresentry.domain.model.Notification
import com.example.depresentry.domain.repository.GeminiRepository
import javax.inject.Inject

class GenerateNotificationsUseCase @Inject constructor(
    private val repository: GeminiRepository
) {
    suspend operator fun invoke(): Result<List<Notification>> {
        return repository.generateNotifications()
    }
} 
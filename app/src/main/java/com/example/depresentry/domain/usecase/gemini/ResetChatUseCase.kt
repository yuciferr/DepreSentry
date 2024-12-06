package com.example.depresentry.domain.usecase.gemini

import com.example.depresentry.domain.repository.GeminiRepository
import javax.inject.Inject

class ResetChatUseCase @Inject constructor(
    private val repository: GeminiRepository
) {
    operator fun invoke() {
        repository.resetChat()
    }
} 
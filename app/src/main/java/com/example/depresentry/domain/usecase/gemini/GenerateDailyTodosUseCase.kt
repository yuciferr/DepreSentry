package com.example.depresentry.domain.usecase.gemini

import com.example.depresentry.domain.model.Task
import com.example.depresentry.domain.repository.GeminiRepository
import javax.inject.Inject

class GenerateDailyTodosUseCase @Inject constructor(
    private val repository: GeminiRepository
) {
    suspend operator fun invoke(): Result<List<Task>> {
        return repository.generateDailyTodos()
    }
} 
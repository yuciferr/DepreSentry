package com.example.depresentry.domain.usecase.userData.local

import com.example.depresentry.domain.repository.UserDataRepository
import javax.inject.Inject

class UpdatePHQ9UseCase @Inject constructor(
    private val repository: UserDataRepository
) {
    suspend operator fun invoke(userId: String, score: Int, answers: List<Int>) {
        repository.updatePHQ9(userId, score, answers)
    }
} 
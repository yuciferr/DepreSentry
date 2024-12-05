package com.example.depresentry.domain.usecase.userData

import com.example.depresentry.domain.model.PHQ9Result
import com.example.depresentry.domain.repository.UserDataRepository
import javax.inject.Inject

class SavePHQ9ResultUseCase @Inject constructor(
    private val repository: UserDataRepository
) {
    suspend operator fun invoke(userId: String, phq9Result: PHQ9Result): Result<Boolean> {
        return repository.savePHQ9Result(userId, phq9Result)
    }
} 
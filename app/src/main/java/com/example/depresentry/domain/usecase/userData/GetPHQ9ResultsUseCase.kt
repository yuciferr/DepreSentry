package com.example.depresentry.domain.usecase.userData

import com.example.depresentry.domain.model.PHQ9Result
import com.example.depresentry.domain.repository.UserDataRepository
import javax.inject.Inject

class GetPHQ9ResultsUseCase @Inject constructor(
    private val repository: UserDataRepository
) {
    suspend operator fun invoke(userId: String): Result<List<PHQ9Result>> {
        return repository.getPHQ9Results(userId)
    }
} 
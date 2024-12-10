package com.example.depresentry.domain.usecase.userData.local

import com.example.depresentry.data.local.entity.DailyDataEntity
import com.example.depresentry.domain.repository.UserDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllLocalDailyDataUseCase @Inject constructor(
    private val repository: UserDataRepository
) {
    operator fun invoke(userId: String): Flow<List<DailyDataEntity>> {
        return repository.getAllLocalDailyData(userId)
    }
} 
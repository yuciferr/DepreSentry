package com.example.depresentry.domain.usecase.userData.local

import com.example.depresentry.data.local.entity.DailyDataEntity
import com.example.depresentry.domain.repository.UserDataRepository
import javax.inject.Inject

class InsertLocalDailyDataUseCase @Inject constructor(
    private val repository: UserDataRepository
) {
    suspend operator fun invoke(dailyData: DailyDataEntity) {
        repository.insertLocalDailyData(dailyData)
    }
} 
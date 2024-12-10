package com.example.depresentry.domain.usecase.userData.local

import com.example.depresentry.data.local.entity.DailyDataEntity
import com.example.depresentry.domain.repository.UserDataRepository
import java.time.LocalDate
import javax.inject.Inject

class GetLocalDailyDataByDateUseCase @Inject constructor(
    private val repository: UserDataRepository
) {
    suspend operator fun invoke(userId: String, date: LocalDate): DailyDataEntity? {
        return repository.getLocalDailyDataByDate(userId, date)
    }
} 
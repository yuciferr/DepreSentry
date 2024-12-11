package com.example.depresentry.domain.usecase.userData.local

import com.example.depresentry.data.local.entity.ChatMessageEntity
import com.example.depresentry.domain.repository.UserDataRepository
import java.time.LocalDate
import javax.inject.Inject

class GetLocalMessageByDateAndTypeAndRoleUseCase @Inject constructor(
    private val repository: UserDataRepository
) {
    suspend operator fun invoke(
        userId: String,
        date: LocalDate,
        messageType: String,
        role: String
    ): ChatMessageEntity? {
        return repository.getLocalMessageByDateAndTypeAndRole(userId, date, messageType, role)
    }
} 
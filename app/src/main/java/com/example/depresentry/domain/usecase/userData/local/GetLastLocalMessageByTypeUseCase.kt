package com.example.depresentry.domain.usecase.userData.local

import com.example.depresentry.data.local.entity.ChatMessageEntity
import com.example.depresentry.domain.repository.UserDataRepository
import javax.inject.Inject

class GetLastLocalMessageByTypeUseCase @Inject constructor(
    private val repository: UserDataRepository
) {
    suspend operator fun invoke(userId: String, messageType: String): ChatMessageEntity? {
        return repository.getLastLocalMessageByType(userId, messageType)
    }
} 
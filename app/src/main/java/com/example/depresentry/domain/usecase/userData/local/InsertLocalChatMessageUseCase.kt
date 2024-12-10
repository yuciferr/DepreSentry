package com.example.depresentry.domain.usecase.userData.local

import com.example.depresentry.data.local.entity.ChatMessageEntity
import com.example.depresentry.domain.repository.UserDataRepository
import javax.inject.Inject

class InsertLocalChatMessageUseCase @Inject constructor(
    private val repository: UserDataRepository
) {
    suspend operator fun invoke(message: ChatMessageEntity) {
        repository.insertLocalChatMessage(message)
    }
} 
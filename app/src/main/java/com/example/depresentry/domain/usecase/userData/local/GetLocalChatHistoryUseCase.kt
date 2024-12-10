package com.example.depresentry.domain.usecase.userData.local

import com.example.depresentry.data.local.entity.ChatMessageEntity
import com.example.depresentry.domain.repository.UserDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocalChatHistoryUseCase @Inject constructor(
    private val repository: UserDataRepository
) {
    operator fun invoke(userId: String): Flow<List<ChatMessageEntity>> {
        return repository.getLocalChatHistory(userId)
    }
} 
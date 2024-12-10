package com.example.depresentry.domain.usecase.userData.local

import com.example.depresentry.data.local.entity.ChatMessageEntity
import com.example.depresentry.domain.repository.UserDataRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class GetLocalChatHistoryByDateUseCase @Inject constructor(
    private val repository: UserDataRepository
) {
    operator fun invoke(userId: String, date: LocalDate): Flow<List<ChatMessageEntity>> {
        return repository.getLocalChatHistoryByDate(userId, date)
    }
} 
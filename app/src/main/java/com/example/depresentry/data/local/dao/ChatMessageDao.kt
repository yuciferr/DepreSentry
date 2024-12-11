package com.example.depresentry.data.local.dao

import androidx.room.*
import com.example.depresentry.data.local.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ChatMessageDao {
    @Insert
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("SELECT * FROM chat_messages WHERE userId = :userId ORDER BY timestamp ASC")
    fun getChatHistory(userId: String): Flow<List<ChatMessageEntity>>

    @Query("DELETE FROM chat_messages WHERE userId = :userId")
    suspend fun clearChatHistory(userId: String)

    @Query("""
        SELECT * FROM chat_messages 
        WHERE userId = :userId 
        AND date = :date 
        AND messageType = :messageType 
        AND role = :role 
        ORDER BY timestamp DESC 
        LIMIT 1
    """)
    suspend fun getMessageByDateAndTypeAndRole(
        userId: String,
        date: LocalDate,
        messageType: String,
        role: String
    ): ChatMessageEntity?
} 
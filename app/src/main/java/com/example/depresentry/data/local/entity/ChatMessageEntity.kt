package com.example.depresentry.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val role: String, // "user" veya "model"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val date: LocalDate = LocalDate.now(),
    val messageType: String, // "profile", "daily_data", "welcome", "affirmation" gibi
    val userId: String // Hangi kullanıcıya ait olduğunu belirtmek için
) 
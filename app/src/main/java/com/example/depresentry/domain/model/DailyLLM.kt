package com.example.depresentry.domain.model

data class DailyLLM(
    val tasks: List<Task>,
    val messages: Messages,
    val notifications: List<Notification>
)

data class Task(
    val title: String,
    val body: String,
    val status: String
)

data class Messages(
    val welcome: String,
    val affirmation: String
)

data class Notification(
    val title: String,
    val body: String,
    val pushingTime: String
) 
package com.example.depresentry.domain.model

data class DailyLLM(
    val messages: Map<String, String>,
    val tasks: List<Task>,
    val notifications: List<Notification>
)

data class Task(
    val title: String,
    val body: String,
    val status: String
)

data class Notification(
    val title: String,
    val body: String,
    val pushingTime: String
) 
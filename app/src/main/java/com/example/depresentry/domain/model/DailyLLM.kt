package com.example.depresentry.domain.model

data class DailyLLM(
    val date: String = "",
    val messages: Map<String, String> = emptyMap(),
    val tasks: List<Task> = emptyList(),
    val notifications: List<Notification> = emptyList()
)

data class Task(
    val title: String = "",
    val body: String = "",
    val status: String = ""
)

data class Notification(
    val title: String = "",
    val body: String = "",
    val pushingTime: String = ""
) 
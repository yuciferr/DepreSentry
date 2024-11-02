package com.example.depresentry.domain.model

data class UserCredentials(
    val email: String,
    val password: String,
    val fullName: String?
)

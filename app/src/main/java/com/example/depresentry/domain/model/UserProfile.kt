package com.example.depresentry.domain.model

data class UserProfile(
    val userId: String = "",
    val fullName: String? = null,
    val age: Int? = null,
    val profession: String? = null,
    val gender: String? = null,
    val maritalStatus: String? = null,
    val country: String? = null,
    val profileImage: String = ""
)


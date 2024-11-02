package com.example.depresentry.domain.model

data class UserProfile(
    val userId: String,  // Firebase UID
    val fullName: String?,
    val gender: String?,
    val age: Int?,
    val profession: String?,
    val maritalStatus: String?,
    val country: String?,
    val profileImage: String?  // URL or local path if image upload is implemented
)


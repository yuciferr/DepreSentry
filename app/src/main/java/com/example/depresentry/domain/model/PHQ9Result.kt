package com.example.depresentry.domain.model

data class PHQ9Result(
    val score: Int,
    val answers: List<Int>,
    val date: String
) 
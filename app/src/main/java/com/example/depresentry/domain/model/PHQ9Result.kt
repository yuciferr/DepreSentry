package com.example.depresentry.domain.model

data class PHQ9Result(
    val score: Int = 0,
    val answers: List<Int> = emptyList(),
    val date: String = ""
) 
package com.fastcampus.fcboard.dto

data class PostUpdateRequest(
    val title: String,
    val content: String,
    val updatedBy: String
)

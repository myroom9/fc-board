package com.fastcampus.fcboard.dto

data class PostDetailResponse(
    val id: Long,
    val title: String,
    val content: String,
    val createdBy: String,
    val updatedBy: String,
    val createdAt: String
)

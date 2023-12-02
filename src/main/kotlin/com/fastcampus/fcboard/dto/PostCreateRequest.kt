package com.fastcampus.fcboard.dto

data class PostCreateRequest(
    val title: String,
    val content: String,
    val createdBy: String
)

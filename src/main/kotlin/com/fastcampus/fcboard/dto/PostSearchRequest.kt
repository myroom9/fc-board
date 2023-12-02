package com.fastcampus.fcboard.dto

import org.springframework.web.bind.annotation.RequestParam


data class PostSearchRequest(
    @RequestParam
    val title: String?,
    @RequestParam
    val createdBy: String?,
)

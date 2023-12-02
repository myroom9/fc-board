package com.fastcampus.fcboard.controller

import com.fastcampus.fcboard.dto.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
class PostController {

    /**
     * 게시글 생성
     */
    @PostMapping("/posts")
    fun createPost(
        @RequestBody postCreateRequest: PostCreateRequest
    ): Long {
        return 1L
    }


    @PutMapping("/posts/{id}")
    fun updatePost(
        @PathVariable id: Long,
        @RequestBody postUpdateRequest: PostUpdateRequest
    ): Long {
        return 1L
    }

    @DeleteMapping("/posts/{id}")
    fun deletePost(
        @PathVariable id: Long,
        @RequestParam createdBy: String
    ): Long {
        println(createdBy)
        return 1L
    }

    @GetMapping("/posts/{id}")
    fun getPost(
        @PathVariable id: Long
    ): PostDetailResponse{
        return PostDetailResponse(1L, "title", "content", "createdBy", "updatedBy", LocalDateTime.now().toString())
    }

    @GetMapping("/posts")
    fun getPosts(
        pageable: Pageable,
        postSearchRequest: PostSearchRequest
    ): Page<PostSummaryResponse> {
        println("title: ${postSearchRequest.title}")
        println("createdBy: ${postSearchRequest.createdBy}")
        return Page.empty()
    }
}

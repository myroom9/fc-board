package com.fastcampus.fcboard.controller

import com.fastcampus.fcboard.controller.dto.*
import com.fastcampus.fcboard.service.PostService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
class PostController(
    private val postService: PostService
) {

    /**
     * 게시글 생성
     */
    @PostMapping("/posts")
    fun createPost(
        @RequestBody postCreateRequest: PostCreateRequest
    ): Long {
        return postService.createPost(postCreateRequest.toDto())
    }

    @PutMapping("/posts/{id}")
    fun updatePost(
        @PathVariable id: Long,
        @RequestBody postUpdateRequest: PostUpdateRequest
    ): Long {
        return postService.updatePost(id, postUpdateRequest.toDto())
    }

    @DeleteMapping("/posts/{id}")
    fun deletePost(
        @PathVariable id: Long,
        @RequestParam createdBy: String
    ): Long {
        println(createdBy)
        return postService.deletePost(id, createdBy)
    }

    @GetMapping("/posts/{id}")
    fun getPost(
        @PathVariable id: Long
    ): PostDetailResponse {
        return postService.getPost(id).toResponse()
    }

    @GetMapping("/posts")
    fun getPosts(
        pageable: Pageable,
        postSearchRequest: PostSearchRequest
    ): Page<PostSummaryResponse> {
        return postService.findPageBy(pageable, postSearchRequest.toDto()).toResponse()
    }
}

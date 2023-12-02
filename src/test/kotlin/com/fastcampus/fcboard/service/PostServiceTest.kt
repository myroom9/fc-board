package com.fastcampus.fcboard.service

import com.fastcampus.fcboard.domain.Post
import com.fastcampus.fcboard.exception.PostNotDeletableException
import com.fastcampus.fcboard.exception.PostNotFoundException
import com.fastcampus.fcboard.exception.PostNotUpdatableException
import com.fastcampus.fcboard.repository.PostRepository
import com.fastcampus.fcboard.service.dto.PostCreateRequestDto
import com.fastcampus.fcboard.service.dto.PostUpdateRequestDto
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull

@SpringBootTest
class PostServiceTest(
    private val postService: PostService,
    private val postRepository: PostRepository
) : BehaviorSpec({
    given("게시글 생성 시") {
        When("게시글 인풋이 정상적으로 들어오면") {
            val postId = postService.createPost(
                PostCreateRequestDto(
                    title = "제목",
                    content = "내용",
                    createdBy = "whahn"
                )
            )
            then("게시글이 정상적으로 생성됨을 확인한다.") {
                postId shouldBeGreaterThan 0L
                val post = postRepository.findByIdOrNull(postId)
                post shouldNotBe null
                post?.title shouldBe "제목"
                post?.content shouldBe "내용"
                post?.createdBy shouldBe "whahn"
            }
        }
    }
    given("게시글 수정시") {
        val saved = postRepository.save(
            Post(
                title = "제목",
                content = "내용",
                createdBy = "whahn"
            )
        )

        When("정상 수정시") {
            val updatedId = postService.updatePost(
                saved.id,
                PostUpdateRequestDto(
                    title = "update title",
                    content = "update content",
                    updatedBy = "whahn"
                )
            )
            then("게시글이 정상적으로 수정됨을 확인한다") {
                saved.id shouldBe updatedId
                val updated = postRepository.findByIdOrNull(updatedId)
                updated shouldNotBe null
                updated?.title shouldBe "update title"
                updated?.content shouldBe "update content"
                updated?.updatedBy shouldBe "update whahn"
            }
        }
        When("게시글을 찾을 수 없을 때") {
            postService.updatePost(
                9999L,
                PostUpdateRequestDto(
                    title = "update title",
                    content = "update content",
                    updatedBy = "whahn"
                )
            )
            then("게시글을 찾을 수 없다라는 예외가 발생한다") {
                shouldThrow<PostNotFoundException> {
                    postService.updatePost(
                        9999L,
                        PostUpdateRequestDto(
                            title = "update title",
                            content = "update content",
                            updatedBy = "whahn"
                        )
                    )
                }
            }
        }
        When("작성자가 동일하지 않으면") {
            then("수정할 수 없는 게시물입니다 예외가 발생한다") {
                shouldThrow<PostNotUpdatableException> {
                    postService.updatePost(
                        9999L,
                        PostUpdateRequestDto(
                            title = "update title",
                            content = "update content",
                            updatedBy = "whahn"
                        )
                    )
                }
            }
        }
    }
    given("게시글 삭제시") {
        val saved = postRepository.save(
            Post(
                title = "제목",
                content = "내용",
                createdBy = "whahn"
            )
        )

        When("정상 삭제시") {
            val postId = postService.deletePost(saved.id, "whahn")
            then("게시글이 정상적으로 삭제됨을 확인한다") {
                postId shouldBe saved.id
                val deleted = postRepository.findByIdOrNull(postId)
                deleted shouldBe null
            }
        }
        When("작성자가 동일하지않으면") {
            val saved2 = postRepository.save(
                Post(
                    title = "제목",
                    content = "내용",
                    createdBy = "whahn"
                )
            )
            then("삭제할 수 없는 게시물입니다 예외가 발생한다") {
                shouldThrow<PostNotDeletableException> {
                    postService.deletePost(saved2.id, "whahn")
                }
            }
        }
    }
})

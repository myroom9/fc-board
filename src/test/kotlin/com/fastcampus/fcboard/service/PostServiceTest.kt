package com.fastcampus.fcboard.service

import com.fastcampus.fcboard.domain.Comment
import com.fastcampus.fcboard.domain.Post
import com.fastcampus.fcboard.domain.Tag
import com.fastcampus.fcboard.exception.PostNotDeletableException
import com.fastcampus.fcboard.exception.PostNotFoundException
import com.fastcampus.fcboard.exception.PostNotUpdatableException
import com.fastcampus.fcboard.repository.CommentRepository
import com.fastcampus.fcboard.repository.PostRepository
import com.fastcampus.fcboard.repository.TagRepository
import com.fastcampus.fcboard.service.dto.PostCreateRequestDto
import com.fastcampus.fcboard.service.dto.PostSearchRequestDto
import com.fastcampus.fcboard.service.dto.PostUpdateRequestDto
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.testcontainers.perSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.testcontainers.containers.GenericContainer

@SpringBootTest
class PostServiceTest(
    private val postService: PostService,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val tagRepository: TagRepository,
    private val likeService: LikeService
) : BehaviorSpec({

    val redisContainer = GenericContainer<Nothing>("redis:5.0.3-alpine")

    afterSpec {
        redisContainer.stop()
    }

    beforeSpec {

        redisContainer.portBindings.add("16379:6379")
        redisContainer.start()
        listener(redisContainer.perSpec())

        postRepository.saveAll(
            listOf(
                Post(
                    title = "제목1",
                    content = "내용1",
                    createdBy = "whahn1",
                    tags = listOf("tag1", "tag2")
                ),
                Post(
                    title = "제목2",
                    content = "내용2",
                    createdBy = "whahn",
                    tags = listOf("tag1", "tag2")
                ),
                Post(
                    title = "제목3",
                    content = "내용3",
                    createdBy = "whahn2",
                    tags = listOf("tag1", "tag2")
                ),
                Post(
                    title = "제목4",
                    content = "내용4",
                    createdBy = "whahn",
                    tags = listOf("tag1", "tag2")
                ),
                Post(
                    title = "제목5",
                    content = "내용5",
                    createdBy = "whahn",
                    tags = listOf("tag1", "tag2")
                ),
                Post(
                    title = "제목6",
                    content = "내용6",
                    createdBy = "whahn",
                    tags = listOf("tag1", "tag5")
                ),
                Post(
                    title = "제목7",
                    content = "내용7",
                    createdBy = "whahn",
                    tags = listOf("tag1", "tag5")
                ),
                Post(
                    title = "제목8",
                    content = "내용8",
                    createdBy = "whahn",
                    tags = listOf("tag1", "tag5")
                ),
                Post(
                    title = "제목9",
                    content = "내용9",
                    createdBy = "whahn",
                    tags = listOf("tag1", "tag5")
                )
            )
        )
    }

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
        When("태그가 추가되면") {
            val postId = postService.createPost(
                PostCreateRequestDto(
                    title = "제목",
                    content = "내용",
                    createdBy = "whahn",
                    tags = listOf("tag1", "tag2")
                )
            )
            then("태그가 정상적으로 추가됨을 확인한다") {
                val tags = tagRepository.findByPostId(postId)
                tags.size shouldBe 2
                tags[0].name shouldBe "tag1"
                tags[1].name shouldBe "tag2"
            }
        }
    }
    given("게시글 수정시") {
        val saved = postRepository.save(
            Post(
                title = "제목",
                content = "내용",
                createdBy = "whahn",
                tags = listOf("tag1", "tag2")
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
                updated?.updatedBy shouldBe "whahn"
            }
        }
        When("게시글을 찾을 수 없을 때") {
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
                        saved.id,
                        PostUpdateRequestDto(
                            title = "update title",
                            content = "update content",
                            updatedBy = "update whahn"
                        )
                    )
                }
            }
        }
        When("태그가 수정되었을 때") {
            val updatedId = postService.updatePost(
                saved.id,
                PostUpdateRequestDto(
                    title = "update title",
                    content = "update content",
                    updatedBy = "whahn",
                    tags = listOf("tag1", "tag2", "tag3")
                )
            )
            then("정상적으로 수정됨을 확인한다") {
                val tags = tagRepository.findByPostId(updatedId)
                tags.size shouldBe 3
                tags[2].name shouldBe "tag3"
            }
            then("태그 순서가 변경되었을 때 정상적으로 변경됨을 확인한다") {
                postService.updatePost(
                    saved.id,
                    PostUpdateRequestDto(
                        title = "update title",
                        content = "update content",
                        updatedBy = "whahn",
                        tags = listOf("tag3", "tag2", "tag1")
                    )
                )

                val tags = tagRepository.findByPostId(updatedId)
                tags.size shouldBe 3
                tags[2].name shouldBe "tag1"
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
                    postService.deletePost(saved2.id, "delete whahn")
                }
            }
        }
    }
    given("게시글 상세조회시") {
        val saved = postRepository.save(
            Post(
                title = "제목",
                content = "내용",
                createdBy = "whahn"
            )
        )

        tagRepository.saveAll(
            listOf(
                Tag(name = "tag1", post = saved, createdBy = "whahn"),
                Tag(name = "tag2", post = saved, createdBy = "whahn"),
                Tag(name = "tag3", post = saved, createdBy = "whahn")
            )
        )
        likeService.createLike(saved.id, "whahn")
        likeService.createLike(saved.id, "whahn1")
        likeService.createLike(saved.id, "whahn2")
        When("정상 조회시") {
            val post = postService.getPost(saved.id)
            then("게시글의 내용이 정상적으로 반환됨을 확인한다.") {
                post.id shouldBe saved.id
                post.title shouldBe "제목"
                post.content shouldBe "내용"
                post.createdBy shouldBe "whahn"
            }
            then("태그가 정상적으로 조회됨을 확인한다") {
                post.tags.size shouldBe 3
                post.tags[0] shouldBe "tag1"
                post.tags[1] shouldBe "tag2"
                post.tags[2] shouldBe "tag3"
            }
            then("좋아요 개수가 조회됨을 확인한다") {
                post.likeCount shouldBe 3
            }
        }
        When("게시글이 없을 때") {
            then("게시글을 찾을 수 없다라는 예외가 발생한다") {
                shouldThrow<PostNotFoundException> {
                    postService.getPost(9999L)
                }
            }
        }
        When("댓글 추가시") {
            commentRepository.save(Comment(content = "댓글 내용", post = saved, createdBy = "댓글 작성자"))
            commentRepository.save(Comment(content = "댓글 내용2", post = saved, createdBy = "댓글 작성자"))
            commentRepository.save(Comment(content = "댓글 내용3", post = saved, createdBy = "댓글 작성자"))
            val post = postService.getPost(saved.id)
            then("댓글이 함께 조회됨을 확인한다") {
                post.comments.size shouldBe 3
                post.comments[0].content shouldBe "댓글 내용"
                post.comments[1].content shouldBe "댓글 내용2"
                post.comments[2].content shouldBe "댓글 내용3"

                post.comments[0].createdBy shouldBe "댓글 작성자"
                post.comments[1].createdBy shouldBe "댓글 작성자"
                post.comments[2].createdBy shouldBe "댓글 작성자"
            }
        }
    }
    given("게시글 목록조회시") {
        When("정상 조회시") {
            val postPage = postService.findPageBy(PageRequest.of(0, 5), PostSearchRequestDto())
            then("게시글 페이지가 반환된다.") {
                postPage.number shouldBe 0
                postPage.size shouldBe 5
                postPage.content.size shouldBe 5
                postPage.content[0].title shouldContain "제목"
                postPage.content[0].createdBy shouldContain "whahn"
            }
        }

        When("타이틀로 검색") {
            val postPage = postService.findPageBy(PageRequest.of(0, 5), PostSearchRequestDto(title = "제목1"))
            then("타이틀에 해당하는 게시글이 반환된다") {
                postPage.number shouldBe 0
                postPage.size shouldBe 5
                postPage.content.size shouldBe 1
                postPage.content[0].title shouldBe "제목1"
                postPage.content[0].createdBy shouldContain "whahn1"
            }
        }

        When("작성자로 검색") {
            val postPage = postService.findPageBy(PageRequest.of(0, 5), PostSearchRequestDto(createdBy = "whahn1"))
            println("postPage = $postPage")
            then("작성자에 해당하는 게시글이 반환된다") {
                postPage.number shouldBe 0
                postPage.size shouldBe 5
                postPage.content.size shouldBe 1
                postPage.content[0].title shouldContain "제목1"
                postPage.content[0].createdBy shouldBe "whahn1"
            }
            then("첫번째 태그가 함께 조회됨을 확인한다") {
                postPage.content.forEach {
                    it.firstTag shouldBe "tag1"
                }
            }
        }
        When("태그로 검색") {
            val postPage = postService.findPageBy(PageRequest.of(0, 5), PostSearchRequestDto(tag = "tag5"))
            then("태그에 해당하는 게시글이 반환된다") {
                postPage.number shouldBe 0
                postPage.size shouldBe 5
                postPage.content.size shouldBe 4
                postPage.content[0].title shouldBe "제목9"
                postPage.content[1].title shouldBe "제목8"
                postPage.content[2].title shouldBe "제목7"
                postPage.content[3].title shouldBe "제목6"
            }
        }
        When("좋아요가 2개 추가되었을 떄") {
            val postPage = postService.findPageBy(PageRequest.of(0, 5), PostSearchRequestDto(tag = "tag5"))
            postPage.content.forEach {
                likeService.createLike(it.id, "whahn")
                likeService.createLike(it.id, "whahn2")
            }

            val likedPostPage = postService.findPageBy(PageRequest.of(0, 5), PostSearchRequestDto(tag = "tag5"))

            then("좋아요 개수가 정상적으로 조회됨을 확인한다") {
                likedPostPage.content.forEach {
                    it.likeCount shouldBe 2
                }
            }
        }
    }
})

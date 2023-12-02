package com.fastcampus.fcboard.exception

open class PostException(
    message: String
) : RuntimeException(message)

class PostNotFoundException(
    message: String = "게시글을 찾을 수 없습니다."
) : PostException(message)

class PostNotUpdatableException(
    message: String = "게시글을 수정할 수 없습니다."
) : PostException(message)

class PostNotDeletableException(
    message: String = "게시글을 삭제하실 수 없습니다"
) : PostException(message)

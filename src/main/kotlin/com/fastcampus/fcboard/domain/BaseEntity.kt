package com.fastcampus.fcboard.domain

import jakarta.persistence.MappedSuperclass
import java.time.LocalDateTime

@MappedSuperclass
abstract class BaseEntity(
    createBy: String
) {
    val createdBy: String = createBy
    val createdAt: LocalDateTime = LocalDateTime.now()
    var updatedBy: String? = null
        protected set
    var updatedAt: LocalDateTime? = null
        protected set

    fun updatedBy(updateBy: String) {
        this.updatedBy = updateBy
        this.updatedAt = LocalDateTime.now()
    }
}

package com.aymanegeek.imedia.user.application.dto

import com.aymanegeek.imedia.user.domain.Role
import java.time.LocalDateTime
import java.util.*

data class UserResponse(
    val id: UUID,
    val email: String,
    val name: String,
    val role: Role,
    val createdAt: LocalDateTime?
)

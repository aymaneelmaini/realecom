package com.aymanegeek.imedia.user.application.service

import arrow.core.Either
import com.aymanegeek.imedia.user.application.dto.RegisterRequest
import com.aymanegeek.imedia.user.application.dto.UserResponse
import com.aymanegeek.imedia.user.domain.UserError
import java.util.*

interface UserService {
    fun register(request: RegisterRequest): Either<UserError, UserResponse>
    fun findById(id: UUID): Either<UserError, UserResponse>
    fun findByEmail(email: String): Either<UserError, UserResponse>
}

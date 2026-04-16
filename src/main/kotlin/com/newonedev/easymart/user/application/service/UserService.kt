package com.newonedev.easymart.user.application.service

import arrow.core.Either
import com.newonedev.easymart.user.application.dto.RegisterRequest
import com.newonedev.easymart.user.application.dto.UserResponse
import com.newonedev.easymart.user.domain.UserError
import java.util.*

interface UserService {
    fun register(request: RegisterRequest): Either<UserError, UserResponse>
    fun findById(id: UUID): Either<UserError, UserResponse>
    fun findByEmail(email: String): Either<UserError, UserResponse>
}

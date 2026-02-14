package com.aymanegeek.imedia.user.domain

import com.aymanegeek.imedia.common.error.DomainError
import org.springframework.http.HttpStatus
import java.util.*

sealed interface UserError : DomainError {

    data class UserNotFound(val userId: UUID) : UserError {
        override val message = "User with id '$userId' not found"
        override val errorCode = "USER_NOT_FOUND"
        override val httpStatus = HttpStatus.NOT_FOUND
    }

    data class UserNotFoundByEmail(val email: String) : UserError {
        override val message = "User with email '$email' not found"
        override val errorCode = "USER_NOT_FOUND"
        override val httpStatus = HttpStatus.NOT_FOUND
    }

    data class EmailAlreadyExists(val email: String) : UserError {
        override val message = "User with email '$email' already exists"
        override val errorCode = "EMAIL_ALREADY_EXISTS"
        override val httpStatus = HttpStatus.CONFLICT
    }

    data class InvalidEmail(val reason: String) : UserError {
        override val message = "Invalid email: $reason"
        override val errorCode = "INVALID_EMAIL"
        override val httpStatus = HttpStatus.UNPROCESSABLE_ENTITY
    }

    data class InvalidPassword(val reason: String) : UserError {
        override val message = "Invalid password: $reason"
        override val errorCode = "INVALID_PASSWORD"
        override val httpStatus = HttpStatus.UNPROCESSABLE_ENTITY
    }

    data class InvalidCredentials(val email: String) : UserError {
        override val message = "Invalid email or password"
        override val errorCode = "INVALID_CREDENTIALS"
        override val httpStatus = HttpStatus.UNAUTHORIZED
    }

    data class Unauthorized(val reason: String) : UserError {
        override val message = "Unauthorized: $reason"
        override val errorCode = "UNAUTHORIZED"
        override val httpStatus = HttpStatus.UNAUTHORIZED
    }

    data class Forbidden(val reason: String) : UserError {
        override val message = "Forbidden: $reason"
        override val errorCode = "FORBIDDEN"
        override val httpStatus = HttpStatus.FORBIDDEN
    }
}

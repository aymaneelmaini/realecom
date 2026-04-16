package com.newonedev.easymart.user.application.service.impl

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.newonedev.easymart.user.application.dto.RegisterRequest
import com.newonedev.easymart.user.application.dto.UserResponse
import com.newonedev.easymart.user.application.service.UserService
import com.newonedev.easymart.user.domain.User
import com.newonedev.easymart.user.domain.UserError
import com.newonedev.easymart.user.domain.UserId
import com.newonedev.easymart.user.domain.UserRepository
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.jvm.optionals.getOrNull


@Transactional
open class DefaultUserService(
    private val userRepository: UserRepository,
    private val jdbcTemplate: JdbcAggregateTemplate,
    private val passwordEncoder: PasswordEncoder
) : UserService {

    override fun register(request: RegisterRequest): Either<UserError, UserResponse> = either {
        val (email, password, name) = request

        ensure(!userRepository.existsByEmail(email)) {
            UserError.EmailAlreadyExists(email)
        }

        ensure(password.length >= 8) {
            UserError.InvalidPassword("Password must be at least 8 characters")
        }

        val hashedPassword = passwordEncoder.encode(password)!!

        val user = User.create(
            email = email,
            password = hashedPassword,
            name = name
        )

        val saved = jdbcTemplate.insert(user)
        saved.toResponse()
    }

    override fun findById(id: UUID): Either<UserError, UserResponse> = either {
        val userId = UserId(id)

        val user = userRepository.findById(userId)
            .orElse(null) ?: raise(UserError.UserNotFound(id))

        user.toResponse()
    }

    override fun findByEmail(email: String): Either<UserError, UserResponse> = either {
        val user = userRepository.findByEmail(email)
            .getOrNull() ?: raise(UserError.UserNotFoundByEmail(email))

        user.toResponse()
    }

    private fun User.toResponse() = UserResponse(
        id = id.value,
        email = email,
        name = name,
        role = role,
        createdAt = createdAt
    )
}

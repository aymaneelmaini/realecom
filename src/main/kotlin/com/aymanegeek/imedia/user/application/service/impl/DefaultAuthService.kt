package com.aymanegeek.imedia.user.application.service.impl

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.aymanegeek.imedia.user.application.dto.LoginRequest
import com.aymanegeek.imedia.user.application.dto.RefreshTokenRequest
import com.aymanegeek.imedia.user.application.dto.TokenResponse
import com.aymanegeek.imedia.user.application.service.AuthService
import com.aymanegeek.imedia.user.application.service.TokenService
import com.aymanegeek.imedia.user.domain.UserError
import com.aymanegeek.imedia.user.domain.UserId
import com.aymanegeek.imedia.user.domain.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Transactional(readOnly = true)
class DefaultAuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenService: TokenService
) : AuthService {

    override fun login(request: LoginRequest): Either<UserError, TokenResponse> = either {
        val user = userRepository.findByEmail(request.email)
            .getOrNull() ?: raise(UserError.InvalidCredentials(request.email))

        ensure(passwordEncoder.matches(request.password, user.password)) {
            UserError.InvalidCredentials(request.email)
        }

        val accessToken = tokenService.generateAccessToken(user)
        val refreshToken = tokenService.generateRefreshToken(user)

        TokenResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = tokenService.getAccessTokenValiditySeconds()
        )
    }

    override fun refresh(request: RefreshTokenRequest): Either<UserError, TokenResponse> = either {
        val validationResult = tokenService.validateToken(request.refreshToken)

        ensure(validationResult.isValid) {
            UserError.Unauthorized("Invalid refresh token")
        }

        ensure(validationResult.tokenType == "refresh") {
            UserError.Unauthorized("Token is not a refresh token")
        }

        val userId = UserId(UUID.fromString(validationResult.userId!!))
        val user = userRepository.findById(userId)
            .orElse(null) ?: raise(UserError.UserNotFound(userId.value))

        val accessToken = tokenService.generateAccessToken(user)
        val refreshToken = tokenService.generateRefreshToken(user)

        TokenResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = tokenService.getAccessTokenValiditySeconds()
        )
    }
}

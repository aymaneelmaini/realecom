package com.aymanegeek.imedia.user.application.service

import com.aymanegeek.imedia.user.domain.User

interface TokenService {
    fun generateAccessToken(user: User): String
    fun generateRefreshToken(user: User): String
    fun validateToken(token: String): TokenValidationResult
    fun getAccessTokenValiditySeconds(): Long
}

data class TokenValidationResult(
    val isValid: Boolean,
    val userId: String? = null,
    val tokenType: String? = null
)

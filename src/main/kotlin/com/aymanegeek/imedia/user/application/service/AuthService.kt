package com.aymanegeek.imedia.user.application.service

import arrow.core.Either
import com.aymanegeek.imedia.user.application.dto.LoginRequest
import com.aymanegeek.imedia.user.application.dto.RefreshTokenRequest
import com.aymanegeek.imedia.user.application.dto.TokenResponse
import com.aymanegeek.imedia.user.domain.UserError

interface AuthService {
    fun login(request: LoginRequest): Either<UserError, TokenResponse>
    fun refresh(request: RefreshTokenRequest): Either<UserError, TokenResponse>
}

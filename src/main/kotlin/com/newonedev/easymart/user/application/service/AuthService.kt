package com.newonedev.easymart.user.application.service

import arrow.core.Either
import com.newonedev.easymart.user.application.dto.LoginRequest
import com.newonedev.easymart.user.application.dto.RefreshTokenRequest
import com.newonedev.easymart.user.application.dto.TokenResponse
import com.newonedev.easymart.user.domain.UserError

interface AuthService {
    fun login(request: LoginRequest): Either<UserError, TokenResponse>
    fun refresh(request: RefreshTokenRequest): Either<UserError, TokenResponse>
}

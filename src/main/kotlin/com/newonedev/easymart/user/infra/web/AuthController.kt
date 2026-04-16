package com.newonedev.easymart.user.infra.web

import com.newonedev.easymart.common.error.DomainErrorException
import com.newonedev.easymart.user.application.dto.LoginRequest
import com.newonedev.easymart.user.application.dto.RefreshTokenRequest
import com.newonedev.easymart.user.application.dto.TokenResponse
import com.newonedev.easymart.user.application.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<TokenResponse> =
        authService.login(request)
            .fold(
                ifLeft = { error -> throw DomainErrorException(error) },
                ifRight = { ResponseEntity.ok(it) }
            )

    @PostMapping("/refresh")
    fun refresh(@Valid @RequestBody request: RefreshTokenRequest): ResponseEntity<TokenResponse> =
        authService.refresh(request)
            .fold(
                ifLeft = { error -> throw DomainErrorException(error) },
                ifRight = { ResponseEntity.ok(it) }
            )
}

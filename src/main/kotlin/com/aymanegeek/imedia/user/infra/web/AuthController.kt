package com.aymanegeek.imedia.user.infra.web

import com.aymanegeek.imedia.common.error.DomainErrorException
import com.aymanegeek.imedia.user.application.dto.LoginRequest
import com.aymanegeek.imedia.user.application.dto.RefreshTokenRequest
import com.aymanegeek.imedia.user.application.dto.TokenResponse
import com.aymanegeek.imedia.user.application.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<TokenResponse> {
        return authService.login(request)
            .fold(
                ifLeft = { error -> throw DomainErrorException(error) },
                ifRight = { ResponseEntity.ok(it) }
            )
    }

    @PostMapping("/refresh")
    fun refresh(@Valid @RequestBody request: RefreshTokenRequest): ResponseEntity<TokenResponse> {
        return authService.refresh(request)
            .fold(
                ifLeft = { error -> throw DomainErrorException(error) },
                ifRight = { ResponseEntity.ok(it) }
            )
    }
}

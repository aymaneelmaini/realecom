package com.aymanegeek.imedia.user.infra.web

import com.aymanegeek.imedia.common.error.DomainErrorException
import com.aymanegeek.imedia.user.application.dto.RegisterRequest
import com.aymanegeek.imedia.user.application.dto.UserResponse
import com.aymanegeek.imedia.user.application.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService
) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<UserResponse> =
        userService.register(request)
            .fold(
                ifLeft =
                    { error -> throw DomainErrorException(error) },
                ifRight =
                    { ResponseEntity(it, HttpStatus.CREATED) }
            )

    @GetMapping("/{id}")
    fun findById(@PathVariable id: UUID): ResponseEntity<UserResponse> =
        userService.findById(id)
            .fold(
                ifLeft = { error -> throw DomainErrorException(error) },
                ifRight = { ResponseEntity.ok(it) }
            )

    @GetMapping("/email/{email}")
    fun findByEmail(@PathVariable email: String): ResponseEntity<UserResponse> =
        userService.findByEmail(email)
            .fold(
                ifLeft = { error -> throw DomainErrorException(error) },
                ifRight = { ResponseEntity.ok(it) }
            )
}

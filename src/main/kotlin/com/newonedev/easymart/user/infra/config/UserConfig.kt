package com.newonedev.easymart.user.infra.config

import com.newonedev.easymart.user.application.service.AuthService
import com.newonedev.easymart.user.application.service.TokenService
import com.newonedev.easymart.user.application.service.UserService
import com.newonedev.easymart.user.application.service.impl.DefaultAuthService
import com.newonedev.easymart.user.application.service.impl.DefaultUserService
import com.newonedev.easymart.user.domain.UserRepository
import com.newonedev.easymart.user.infra.security.JwtTokenService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder

@Configuration
class UserConfig {

    @Bean
    fun userService(
        userRepository: UserRepository,
        jdbcTemplate: JdbcAggregateTemplate,
        passwordEncoder: PasswordEncoder
    ): UserService = DefaultUserService(
        userRepository,
        jdbcTemplate,
        passwordEncoder
    )

    @Bean
    fun authService(
        userRepository: UserRepository,
        passwordEncoder: PasswordEncoder,
        tokenService: TokenService
    ): AuthService = DefaultAuthService(
        userRepository,
        passwordEncoder,
        tokenService
    )

    @Bean
    fun jwtTokenService(
        jwtEncoder: JwtEncoder,
        jwtDecoder: JwtDecoder
    ): TokenService = JwtTokenService(jwtEncoder, jwtDecoder)
}
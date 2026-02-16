package com.aymanegeek.imedia.user.infra.security

import com.aymanegeek.imedia.user.application.service.TokenService
import com.aymanegeek.imedia.user.application.service.TokenValidationResult
import com.aymanegeek.imedia.user.domain.User
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class JwtTokenService(
    private val jwtEncoder: JwtEncoder,
    private val jwtDecoder: JwtDecoder
) : TokenService {

    override fun generateAccessToken(user: User): String {
        val now = Instant.now()
        val claims = JwtClaimsSet.builder()
            .issuer("imedia")
            .issuedAt(now)
            .expiresAt(now.plus(ACCESS_TOKEN_VALIDITY_MINUTES, ChronoUnit.MINUTES))
            .subject(user.id!!.value.toString())
            .claim("email", user.email)
            .claim("name", user.name)
            .claim("role", user.role.name)
            .build()

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).tokenValue
    }

    override fun generateRefreshToken(user: User): String {
        val now = Instant.now()
        val claims = JwtClaimsSet.builder()
            .issuer("imedia")
            .issuedAt(now)
            .expiresAt(now.plus(REFRESH_TOKEN_VALIDITY_DAYS, ChronoUnit.DAYS))
            .subject(user.id!!.value.toString())
            .claim("type", "refresh")
            .build()

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).tokenValue
    }

    override fun validateToken(token: String): TokenValidationResult =
        try {
            val jwt = jwtDecoder.decode(token)
            TokenValidationResult(
                isValid = true,
                userId = jwt.subject,
                tokenType = jwt.claims["type"] as? String
            )
        } catch (e: Exception) {
            TokenValidationResult(isValid = false)
        }

    override fun getAccessTokenValiditySeconds(): Long = ACCESS_TOKEN_VALIDITY_MINUTES * 60

    companion object {
        const val ACCESS_TOKEN_VALIDITY_MINUTES = 480L // 8 hours
        const val REFRESH_TOKEN_VALIDITY_DAYS = 7L
    }
}

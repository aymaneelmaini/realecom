package com.aymanegeek.imedia.user.infra.seeder

import com.aymanegeek.imedia.user.domain.Role
import com.aymanegeek.imedia.user.domain.Role.*
import com.aymanegeek.imedia.user.domain.User
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
@Profile("init")
class CreateDefaultUsersSeeder(
    private val jdbcTemplate: JdbcAggregateTemplate,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun run(vararg args: String) {
        val user = User.create(
            email = "admin@imedia.com",
            password = passwordEncoder.encode("password")!!,
            name = "admin",
            role = ADMIN
        )
        jdbcTemplate.insert(user)
        logger.info("admin created successfully")
    }
}
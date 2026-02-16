package com.aymanegeek.imedia.user.infra.config

import com.aymanegeek.imedia.user.infra.seeder.CreateDefaultUsersSeeder
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class SeederConfig {

    @Bean
    @Profile("init")
    fun createDefaultUsersSeeder(
        jdbcTemplate: JdbcAggregateTemplate,
        passwordEncoder: PasswordEncoder
    ): CommandLineRunner = CreateDefaultUsersSeeder(jdbcTemplate, passwordEncoder)
}

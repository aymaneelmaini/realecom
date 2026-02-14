package com.aymanegeek.imedia.user.domain

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.ReadOnlyProperty
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.*

@Table(name = "users", schema = "user_schema")
data class User(
    @Id val id: UserId,
    val email: String,
    val password: String,
    val name: String,
    val role: Role = Role.CUSTOMER,
    @ReadOnlyProperty val createdAt: LocalDateTime? = null
) {
    init {
        require(email.isNotBlank()) { "Email must not be blank" }
        require(email.contains("@")) { "Email must be valid" }
        require(name.isNotBlank()) { "Name must not be blank" }
        require(password.isNotBlank()) { "Password must not be blank" }
    }

    fun isAdmin(): Boolean = role == Role.ADMIN

    fun isManager(): Boolean = role == Role.MANAGER

    fun isCustomer(): Boolean = role == Role.CUSTOMER

    companion object {
        fun create(
            email: String,
            password: String,
            name: String,
            role: Role = Role.CUSTOMER
        ) = User(
            id = UserId.generate(),
            email = email,
            password = password,
            name = name,
            role = role,
            createdAt = null
        )
    }
}

enum class Role {
    ADMIN,
    MANAGER,
    CUSTOMER
}

@JvmInline
value class UserId(val value: UUID) {
    companion object {
        fun generate() = UserId(UUID.randomUUID())
    }
}

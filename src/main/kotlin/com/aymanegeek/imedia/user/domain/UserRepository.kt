package com.aymanegeek.imedia.user.domain

import org.springframework.data.repository.CrudRepository
import java.util.*

interface UserRepository : CrudRepository<User, UserId> {
    fun findByEmail(email: String): Optional<User>
    fun existsByEmail(email: String): Boolean
}

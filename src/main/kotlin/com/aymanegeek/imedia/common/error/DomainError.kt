package com.aymanegeek.imedia.common.error

import org.springframework.http.HttpStatus

interface DomainError {
    val message: String
    val httpStatus: HttpStatus
    val errorCode: String
}

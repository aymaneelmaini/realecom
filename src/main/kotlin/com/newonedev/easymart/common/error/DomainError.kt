package com.newonedev.easymart.common.error

import org.springframework.http.HttpStatus

interface DomainError {
    val message: String
    val httpStatus: HttpStatus
    val errorCode: String
    val details: Any? get() = null
}

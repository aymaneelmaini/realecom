package com.aymanegeek.imedia.common.error

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(DomainErrorException::class)
    fun handleDomainError(
        ex: DomainErrorException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val error = ex.error
        logger.warn("Domain error occurred: {}", error)

        val errorResponse = ErrorResponse(
            code = error.httpStatus.value(),
            message = error.message,
            details = error.errorCode,
            timestamp = LocalDateTime.now(),
            url = request.getDescription(false).removePrefix("uri=")
        )

        return ResponseEntity.status(error.httpStatus).body(errorResponse)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(
        ex: MethodArgumentNotValidException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.map {
            ValidationError(
                field = it.field,
                message = it.defaultMessage ?: "Invalid value",
                rejectedValue = it.rejectedValue
            )
        }

        val errorResponse = ErrorResponse(
            code = HttpStatus.BAD_REQUEST.value(),
            message = "Validation failed",
            details = errors,
            timestamp = LocalDateTime.now(),
            url = request.getDescription(false).removePrefix("uri=")
        )

        return ResponseEntity.badRequest().body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected error occurred", ex)

        val errorResponse = ErrorResponse(
            code = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            message = "An unexpected error occurred",
            details = ex.message,
            timestamp = LocalDateTime.now(),
            url = request.getDescription(false).removePrefix("uri=")
        )

        return ResponseEntity.internalServerError().body(errorResponse)
    }

    data class ValidationError(
        val field: String,
        val message: String,
        val rejectedValue: Any? = null
    )

    data class ErrorResponse(
        val code: Int,
        val message: String,
        val details: Any?,
        val timestamp: LocalDateTime,
        val url: String
    )
}

class DomainErrorException(val error: DomainError) : RuntimeException(error.message)

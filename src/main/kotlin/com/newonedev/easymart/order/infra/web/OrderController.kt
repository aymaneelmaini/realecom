package com.newonedev.easymart.order.infra.web

import com.newonedev.easymart.common.error.DomainErrorException
import com.newonedev.easymart.order.application.dto.CreateOrderRequest
import com.newonedev.easymart.order.application.dto.CreateOrderResponse
import com.newonedev.easymart.order.application.usecase.CreateOrderUseCase
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/orders")
class OrderController(
    private val createOrderUseCase: CreateOrderUseCase
) {

    @PostMapping
    fun createOrder(@Valid @RequestBody request: CreateOrderRequest): ResponseEntity<CreateOrderResponse> =
        createOrderUseCase.execute(request)
            .fold(
                ifLeft = { error -> throw DomainErrorException(error) },
                ifRight = { ResponseEntity(it, HttpStatus.CREATED) }
            )
}

package com.aymanegeek.imedia.order.infra.web

import com.aymanegeek.imedia.common.error.DomainErrorException
import com.aymanegeek.imedia.order.application.dto.CreateOrderRequest
import com.aymanegeek.imedia.order.application.dto.CreateOrderResponse
import com.aymanegeek.imedia.order.application.usecase.CreateOrderUseCase
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

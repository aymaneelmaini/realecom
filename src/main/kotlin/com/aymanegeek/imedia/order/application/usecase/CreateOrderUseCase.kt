package com.aymanegeek.imedia.order.application.usecase

import arrow.core.Either
import com.aymanegeek.imedia.order.application.dto.CreateOrderRequest
import com.aymanegeek.imedia.order.application.dto.CreateOrderResponse
import com.aymanegeek.imedia.order.domain.OrderError

interface CreateOrderUseCase {

    fun execute(request: CreateOrderRequest): Either<OrderError, CreateOrderResponse>
}
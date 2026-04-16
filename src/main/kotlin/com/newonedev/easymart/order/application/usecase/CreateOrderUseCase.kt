package com.newonedev.easymart.order.application.usecase

import arrow.core.Either
import com.newonedev.easymart.order.application.dto.CreateOrderRequest
import com.newonedev.easymart.order.application.dto.CreateOrderResponse
import com.newonedev.easymart.order.domain.OrderError

interface CreateOrderUseCase {

    fun execute(request: CreateOrderRequest): Either<OrderError, CreateOrderResponse>
}
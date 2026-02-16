package com.aymanegeek.imedia.order.infra.config

import com.aymanegeek.imedia.inventory.application.usecase.ReserveInventoryUsecase
import com.aymanegeek.imedia.order.application.usecase.CreateOrderUseCase
import com.aymanegeek.imedia.order.application.usecase.UpdateOrderPaymentStatusUseCase
import com.aymanegeek.imedia.order.application.usecase.impl.DefaultCreateOrderUseCase
import com.aymanegeek.imedia.order.application.usecase.impl.DefaultUpdateOrderPaymentStatusUseCase
import com.aymanegeek.imedia.order.domain.OrderRepository
import com.aymanegeek.imedia.payment.domain.PaymentProcessor
import com.aymanegeek.imedia.payment.domain.PaymentRepository
import com.aymanegeek.imedia.product.application.usecase.VerifyProductsExistUsecase
import com.aymanegeek.imedia.product.domain.ProductRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.core.JdbcAggregateTemplate

@Configuration
class OrderConfig {

    @Bean
    fun createOrderUseCase(
        verifyProductExistUsecase: VerifyProductsExistUsecase,
        reserveInventoryUsecase: ReserveInventoryUsecase,
        productRepository: ProductRepository,
        paymentProcessor: PaymentProcessor,
        jdbcTemplate: JdbcAggregateTemplate
    ): CreateOrderUseCase = DefaultCreateOrderUseCase(
        verifyProductExistUsecase,
        reserveInventoryUsecase,
        productRepository,
        paymentProcessor,
        jdbcTemplate
    )

    @Bean
    fun updateOrderPaymentStatusUseCase(
        orderRepository: OrderRepository,
        paymentRepository: PaymentRepository
    ): UpdateOrderPaymentStatusUseCase = DefaultUpdateOrderPaymentStatusUseCase(
        orderRepository,
        paymentRepository
    )
}
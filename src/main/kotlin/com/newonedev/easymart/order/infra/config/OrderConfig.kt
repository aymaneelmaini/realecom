package com.newonedev.easymart.order.infra.config

import com.newonedev.easymart.inventory.application.usecase.ReserveInventoryUsecase
import com.newonedev.easymart.order.application.usecase.CreateOrderUseCase
import com.newonedev.easymart.order.application.usecase.UpdateOrderPaymentStatusUseCase
import com.newonedev.easymart.order.application.usecase.impl.DefaultCreateOrderUseCase
import com.newonedev.easymart.order.application.usecase.impl.DefaultUpdateOrderPaymentStatusUseCase
import com.newonedev.easymart.order.domain.OrderRepository
import com.newonedev.easymart.payment.domain.PaymentProcessor
import com.newonedev.easymart.payment.domain.PaymentRepository
import com.newonedev.easymart.product.application.usecase.VerifyProductsExistUsecase
import com.newonedev.easymart.product.domain.ProductRepository
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
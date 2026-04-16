package com.newonedev.easymart.inventory.infra.config

import com.newonedev.easymart.inventory.application.service.DefaultInventoryService
import com.newonedev.easymart.inventory.application.service.InventoryService
import com.newonedev.easymart.inventory.application.usecase.ReserveInventoryUsecase
import com.newonedev.easymart.inventory.application.usecase.VerifyInventoryAvailabilityUsecase
import com.newonedev.easymart.inventory.application.usecase.impl.DefaultReserveInventoryUsecase
import com.newonedev.easymart.inventory.application.usecase.impl.DefaultVerifyInventoryAvailabilityUsecase
import com.newonedev.easymart.inventory.domain.InventoryRepository
import com.newonedev.easymart.product.domain.ProductRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.core.JdbcAggregateTemplate

@Configuration
class InventoryConfig {

    @Bean
    fun verifyInventoryAvailabilityUsecase(
        inventoryRepository: InventoryRepository
    ): VerifyInventoryAvailabilityUsecase = DefaultVerifyInventoryAvailabilityUsecase(inventoryRepository)

    @Bean
    fun reserveInventoryUsecase(
        inventoryRepository: InventoryRepository,
        verifyInventoryAvailabilityUsecase: VerifyInventoryAvailabilityUsecase
    ): ReserveInventoryUsecase = DefaultReserveInventoryUsecase(
        inventoryRepository,
        verifyInventoryAvailabilityUsecase
    )

    @Bean
    fun inventoryService(
        inventoryRepository: InventoryRepository,
        jdbcTemplate: JdbcAggregateTemplate,
        productRepository: ProductRepository
    ): InventoryService = DefaultInventoryService(
        inventoryRepository,
        jdbcTemplate,
        productRepository
    )
}
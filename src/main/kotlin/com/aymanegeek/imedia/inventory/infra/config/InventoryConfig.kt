package com.aymanegeek.imedia.inventory.infra.config

import com.aymanegeek.imedia.inventory.application.service.DefaultInventoryService
import com.aymanegeek.imedia.inventory.application.service.InventoryService
import com.aymanegeek.imedia.inventory.application.usecase.ReserveInventoryUsecase
import com.aymanegeek.imedia.inventory.application.usecase.VerifyInventoryAvailabilityUsecase
import com.aymanegeek.imedia.inventory.application.usecase.impl.DefaultReserveInventoryUsecase
import com.aymanegeek.imedia.inventory.application.usecase.impl.DefaultVerifyInventoryAvailabilityUsecase
import com.aymanegeek.imedia.inventory.domain.InventoryRepository
import com.aymanegeek.imedia.product.domain.ProductRepository
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
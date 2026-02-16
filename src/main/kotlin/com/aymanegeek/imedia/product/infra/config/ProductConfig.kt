package com.aymanegeek.imedia.product.infra.config

import com.aymanegeek.imedia.product.application.service.DefaultProductService
import com.aymanegeek.imedia.product.application.service.ProductService
import com.aymanegeek.imedia.product.application.usecase.DefaultVerifyProductsExistUsecase
import com.aymanegeek.imedia.product.application.usecase.VerifyProductsExistUsecase
import com.aymanegeek.imedia.product.domain.ProductRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.core.JdbcAggregateTemplate

@Configuration
class ProductConfig {

    @Bean
    fun verifyProductsExistUsecase(productRepository: ProductRepository): VerifyProductsExistUsecase =
        DefaultVerifyProductsExistUsecase(productRepository)

    @Bean
    fun productService(
        productRepository: ProductRepository,
        jdbcTemplate: JdbcAggregateTemplate
    ): ProductService = DefaultProductService(
        productRepository,
        jdbcTemplate
    )
}
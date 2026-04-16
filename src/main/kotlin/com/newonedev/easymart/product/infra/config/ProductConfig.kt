package com.newonedev.easymart.product.infra.config

import com.newonedev.easymart.product.application.service.DefaultProductService
import com.newonedev.easymart.product.application.service.ProductService
import com.newonedev.easymart.product.application.usecase.DefaultVerifyProductsExistUsecase
import com.newonedev.easymart.product.application.usecase.VerifyProductsExistUsecase
import com.newonedev.easymart.product.domain.ProductRepository
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
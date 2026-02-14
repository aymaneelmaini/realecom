package com.aymanegeek.imedia.config

import com.aymanegeek.imedia.product.application.service.DefaultProductService
import com.aymanegeek.imedia.product.application.service.ProductService
import com.aymanegeek.imedia.product.domain.ProductRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ServiceConfig {

    @Bean
    fun productService(productRepository: ProductRepository): ProductService = DefaultProductService(productRepository)
}
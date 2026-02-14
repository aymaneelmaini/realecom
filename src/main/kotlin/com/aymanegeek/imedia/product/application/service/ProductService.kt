package com.aymanegeek.imedia.product.application.service

import arrow.core.Either
import com.aymanegeek.imedia.product.application.dto.CreateProductRequest
import com.aymanegeek.imedia.product.application.dto.ProductResponse
import com.aymanegeek.imedia.product.domain.ProductError
import java.util.*

interface ProductService {
    fun createProduct(request: CreateProductRequest): Either<ProductError, ProductResponse>

    fun getAllProducts(): Either<ProductError, List<ProductResponse>>

    fun getProductById(id: UUID): Either<ProductError, ProductResponse>
}
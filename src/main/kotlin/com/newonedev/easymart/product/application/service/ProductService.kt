package com.newonedev.easymart.product.application.service

import arrow.core.Either
import com.newonedev.easymart.product.application.dto.CreateProductRequest
import com.newonedev.easymart.product.application.dto.ProductResponse
import com.newonedev.easymart.product.domain.ProductError
import java.util.*

interface ProductService {
    fun createProduct(request: CreateProductRequest): Either<ProductError, ProductResponse>

    fun getAllProducts(): Either<ProductError, List<ProductResponse>>

    fun getProductById(id: UUID): Either<ProductError, ProductResponse>
}
package com.aymanegeek.imedia.product.application.usecase

import arrow.core.Either
import com.aymanegeek.imedia.product.domain.ProductError
import com.aymanegeek.imedia.product.domain.ProductId

interface VerifyProductsExistUsecase {

    fun execute(request: VerifyProductsRequest): Either<ProductError, VerifyProductsResponse>
}

data class VerifyProductsRequest(val ids: List<ProductId>)

data class VerifyProductsResponse(
    val existingProducts: Set<ProductId>,
    val missingProducts: Set<ProductId>
) {
    val allExist = missingProducts.isEmpty()
}
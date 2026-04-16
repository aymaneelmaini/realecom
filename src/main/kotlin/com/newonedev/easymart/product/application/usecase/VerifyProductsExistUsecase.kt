package com.newonedev.easymart.product.application.usecase

import arrow.core.Either
import com.newonedev.easymart.product.domain.ProductError
import com.newonedev.easymart.common.vo.ProductId

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
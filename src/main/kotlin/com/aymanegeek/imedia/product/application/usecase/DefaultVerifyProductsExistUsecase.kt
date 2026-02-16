package com.aymanegeek.imedia.product.application.usecase

import arrow.core.Either
import arrow.core.right
import com.aymanegeek.imedia.product.domain.ProductError
import com.aymanegeek.imedia.product.domain.ProductRepository
import org.springframework.stereotype.Service

@Service
class DefaultVerifyProductsExistUsecase(private val repository: ProductRepository) :
    VerifyProductsExistUsecase {
    override fun execute(request: VerifyProductsRequest): Either<ProductError, VerifyProductsResponse> {
        val (existingProducts, missingProducts) = request.ids
            .partition(repository::existsById)

        return VerifyProductsResponse(
            existingProducts = existingProducts.toSet(),
            missingProducts = missingProducts.toSet()
        ).right()
    }

}

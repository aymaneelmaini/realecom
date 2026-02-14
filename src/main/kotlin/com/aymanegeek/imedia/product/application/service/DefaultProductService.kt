package com.aymanegeek.imedia.product.application.service

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.aymanegeek.imedia.common.vo.Price
import com.aymanegeek.imedia.common.vo.PriceDto
import com.aymanegeek.imedia.product.application.dto.CreateProductRequest
import com.aymanegeek.imedia.product.application.dto.ProductResponse
import com.aymanegeek.imedia.product.domain.Product
import com.aymanegeek.imedia.product.domain.ProductError
import com.aymanegeek.imedia.product.domain.ProductId
import com.aymanegeek.imedia.product.domain.ProductRepository
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class DefaultProductService(
    private val productRepository: ProductRepository,
    private val jdbcTemplate: JdbcAggregateTemplate
) : ProductService {

    @Transactional
    override fun createProduct(request: CreateProductRequest): Either<ProductError, ProductResponse> = either {
        val (name, description, priceDto) = request

        ensure(!productRepository.existsByName(name)) {
            ProductError.ProductNameAlreadyExists(name)
        }

        ensure(name.length >= 3) {
            ProductError.InvalidProductName("Name must be at least 3 characters long")
        }

        val product = Product.create(
            name = name,
            description = description,
            price = Price(
                amount = priceDto.amount,
                currency = priceDto.currency
            )
        )

        val savedProduct = jdbcTemplate.insert(product)
        savedProduct.toResponse()
    }

    @Transactional(readOnly = true)
    override fun getAllProducts(): Either<ProductError, List<ProductResponse>> = either {
        productRepository.findAll()
            .map { it.toResponse() }
            .toList()
    }

    @Transactional(readOnly = true)
    override fun getProductById(id: UUID): Either<ProductError, ProductResponse> = either {
        val productId = ProductId(id)

        val product = productRepository.findById(productId)
            .orElse(null) ?: raise(ProductError.ProductNotFound(id))

        product.toResponse()
    }

    private fun Product.toResponse() = ProductResponse(
        id = id.value,
        name = name,
        description = description,
        price = PriceDto(
            amount = price.amount,
            currency = price.currency
        )
    )
}
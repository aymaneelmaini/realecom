package com.aymanegeek.imedia.product.infra.web

import com.aymanegeek.imedia.common.error.DomainErrorException
import com.aymanegeek.imedia.product.application.dto.CreateProductRequest
import com.aymanegeek.imedia.product.application.dto.ProductResponse
import com.aymanegeek.imedia.product.application.service.ProductService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/products")
class ProductController(
    private val productService: ProductService
) {

    @PostMapping
    fun createProduct(@Valid @RequestBody request: CreateProductRequest): ResponseEntity<ProductResponse> =
        productService.createProduct(request)
            .fold(
                ifLeft = { error -> throw DomainErrorException(error) },
                ifRight = { ResponseEntity(it, HttpStatus.CREATED) }
            )

    @GetMapping
    fun getAllProducts(): ResponseEntity<List<ProductResponse>> =
        productService.getAllProducts()
            .fold(
                ifLeft = { error -> throw DomainErrorException(error) },
                ifRight = { ResponseEntity.ok(it) }
            )

    @GetMapping("/{id}")
    fun getProductById(@PathVariable id: UUID): ResponseEntity<ProductResponse> =
        productService.getProductById(id)
            .fold(
                ifLeft = { error -> throw DomainErrorException(error) },
                ifRight = { ResponseEntity.ok(it) }
            )
}

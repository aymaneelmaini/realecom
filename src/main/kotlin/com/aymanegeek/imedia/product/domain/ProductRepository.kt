package com.aymanegeek.imedia.product.domain

import org.springframework.data.repository.CrudRepository

interface ProductRepository : CrudRepository<Product, ProductId> {
    fun existsByName(name: String): Boolean
}
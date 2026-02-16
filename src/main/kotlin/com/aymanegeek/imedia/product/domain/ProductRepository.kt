package com.aymanegeek.imedia.product.domain

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.util.*

interface ProductRepository : CrudRepository<Product, ProductId> {
    fun existsByName(name: String): Boolean

    @Query("SELECT * FROM product_schema.products WHERE id IN (:ids)")
    fun findAllByIdIn(ids: List<UUID>): List<Product>
}
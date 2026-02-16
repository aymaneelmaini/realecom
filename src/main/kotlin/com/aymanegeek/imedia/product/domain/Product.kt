package com.aymanegeek.imedia.product.domain

import com.aymanegeek.imedia.common.vo.Price
import com.aymanegeek.imedia.common.vo.ProductId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.ReadOnlyProperty
import org.springframework.data.relational.core.mapping.Embedded
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty.USE_NULL
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Table(name = "products", schema = "product_schema")
data class Product(
    @Id val id: ProductId,
    val name: String,
    val description: String?,
    @field:Embedded(prefix = "product_price_", onEmpty = USE_NULL)
    val price: Price,
    @ReadOnlyProperty val createdAt: LocalDateTime? = null
) {
    init {
        require(name.isNotBlank()) { "Product name must not be empty" }
        require(price.amount > BigDecimal.ZERO) { "Product price must not be zero" }
    }

    companion object {
        fun create(
            name: String,
            description: String?,
            price: Price
        ) = Product(
            id = ProductId.generate(),
            name = name,
            description = description,
            price = price,
            createdAt = null
        )
    }
}


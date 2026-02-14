package com.aymanegeek.imedia.product.domain

import com.aymanegeek.imedia.common.vo.Price
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Embedded
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty.USE_NULL
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Table("products")
data class Product(
    @Id val id: ProductId? = null,
    val name: String,
    val description: String?,
    @field:Embedded(onEmpty = USE_NULL) val price: Price,
    val createdAt: LocalDateTime? = null
) {
    init {
        require(name.isNotBlank()) { "Product name must not be empty" }
        require(price.amount > BigDecimal.ZERO) { "Product price must not be zero" }
    }
}

@JvmInline
value class ProductId(val value: UUID)


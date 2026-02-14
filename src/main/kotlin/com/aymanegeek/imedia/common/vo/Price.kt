package com.aymanegeek.imedia.common.vo

import java.math.BigDecimal

data class Price(
    val amount: BigDecimal,
    val currency: String = "USD"
) {
    init {
        require(amount > BigDecimal.ZERO) { "Product price must be positive" }
        require(currency.isNotBlank()) { "Product currency must not be blank" }
    }
}
package com.aymanegeek.imedia.common.vo

@JvmInline
value class Quantity(val value: Int) {
    init {
        require(value >= 0) { "Quantity must be non-negative" }
    }

    companion object {
        val ZERO: Quantity = Quantity(0)
    }
}
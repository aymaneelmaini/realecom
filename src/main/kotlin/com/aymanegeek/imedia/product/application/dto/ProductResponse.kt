package com.aymanegeek.imedia.product.application.dto

import com.aymanegeek.imedia.common.vo.PriceDto
import java.util.*

data class ProductResponse(
    val id: UUID,
    val name: String,
    val description: String?,
    val price: PriceDto
)
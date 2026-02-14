package com.aymanegeek.imedia.product.application.dto

import com.aymanegeek.imedia.common.vo.PriceDto
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CreateProductRequest(
    @field:NotBlank val name: String,
    val description: String?,
    @field:NotNull val price: PriceDto
)
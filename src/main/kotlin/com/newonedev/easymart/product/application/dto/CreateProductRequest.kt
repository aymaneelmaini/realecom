package com.newonedev.easymart.product.application.dto

import com.newonedev.easymart.common.vo.PriceDto
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CreateProductRequest(
    @field:NotBlank val name: String,
    val description: String?,
    @field:NotNull val price: PriceDto
)
package com.newonedev.easymart.product.application.dto

import com.newonedev.easymart.common.vo.PriceDto
import java.util.*

data class ProductResponse(
    val id: UUID,
    val name: String,
    val description: String?,
    val price: PriceDto
)
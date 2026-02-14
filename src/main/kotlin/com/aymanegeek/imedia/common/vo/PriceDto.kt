package com.aymanegeek.imedia.common.vo

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import org.hibernate.validator.constraints.Currency
import org.jetbrains.annotations.NotNull
import java.math.BigDecimal

data class PriceDto(
    @field:NotNull @field:Positive val amount: BigDecimal,
    @field:NotBlank @Currency val currency: String
)

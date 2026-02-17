package com.aymanegeek.imedia.product.application.usecase

import com.aymanegeek.imedia.common.vo.ProductId
import com.aymanegeek.imedia.product.domain.ProductRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class DefaultVerifyProductsExistUsecaseTest : StringSpec({

    val repository: ProductRepository = mockk()
    val sut = DefaultVerifyProductsExistUsecase(repository)

    val productA = ProductId.generate()
    val productB = ProductId.generate()

    "returns allExist=true when all products exist" {
        every { repository.existsById(any()) } returns true

        val response = sut.execute(VerifyProductsRequest(listOf(productA, productB))).getOrNull()!!

        response.allExist shouldBe true
        response.missingProducts.shouldBeEmpty()
    }

    "returns allExist=false with the missing products when some don't exist" {
        every { repository.existsById(productA) } returns true
        every { repository.existsById(productB) } returns false

        val response = sut.execute(VerifyProductsRequest(listOf(productA, productB))).getOrNull()!!

        response.allExist shouldBe false
        response.missingProducts shouldBe setOf(productB)
        response.existingProducts shouldBe setOf(productA)
    }

    "returns allExist=true for an empty product list" {
        val response = sut.execute(VerifyProductsRequest(emptyList())).getOrNull()!!

        response.allExist shouldBe true
    }

    "always returns Right, never a domain error" {
        every { repository.existsById(any()) } returns false

        val result = sut.execute(VerifyProductsRequest(listOf(productA)))

        result.isRight() shouldBe true
    }
})

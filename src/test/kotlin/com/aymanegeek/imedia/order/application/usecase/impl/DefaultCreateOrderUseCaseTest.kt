package com.aymanegeek.imedia.order.application.usecase.impl

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.aymanegeek.imedia.common.vo.Price
import com.aymanegeek.imedia.common.vo.ProductId
import com.aymanegeek.imedia.inventory.application.usecase.ReserveInventoryResponse
import com.aymanegeek.imedia.inventory.application.usecase.ReserveInventoryUsecase
import com.aymanegeek.imedia.inventory.domain.InventoryError
import com.aymanegeek.imedia.order.application.dto.CreateOrderRequest
import com.aymanegeek.imedia.order.application.dto.OrderLineRequest
import com.aymanegeek.imedia.order.domain.Order
import com.aymanegeek.imedia.order.domain.OrderError
import com.aymanegeek.imedia.order.domain.OrderId
import com.aymanegeek.imedia.payment.domain.PaymentError
import com.aymanegeek.imedia.payment.domain.PaymentLineItem
import com.aymanegeek.imedia.payment.domain.PaymentLinkResult
import com.aymanegeek.imedia.payment.domain.PaymentProcessor
import com.aymanegeek.imedia.product.application.usecase.VerifyProductsExistUsecase
import com.aymanegeek.imedia.product.application.usecase.VerifyProductsResponse
import com.aymanegeek.imedia.product.domain.Product
import com.aymanegeek.imedia.product.domain.ProductRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import java.math.BigDecimal

private class FakePaymentProcessor : PaymentProcessor {
    var result: Either<PaymentError, PaymentLinkResult> = PaymentLinkResult("", "").right()
    override fun createPaymentLink(orderId: OrderId, lineItems: List<PaymentLineItem>, description: String) = result
}

class DefaultCreateOrderUseCaseTest : StringSpec({

    val verifyProductExistUsecase: VerifyProductsExistUsecase = mockk()
    val reserveInventoryUsecase: ReserveInventoryUsecase = mockk()
    val productRepository: ProductRepository = mockk()
    val jdbcTemplate: JdbcAggregateTemplate = mockk()
    val paymentProcessor = FakePaymentProcessor()

    val sut = DefaultCreateOrderUseCase(
        verifyProductExistUsecase,
        reserveInventoryUsecase,
        productRepository,
        paymentProcessor,
        jdbcTemplate
    )

    val productId = ProductId.generate()
    val product = Product(id = productId, name = "Iphone", description = null, price = Price(BigDecimal("10.00")))
    val orderLine = OrderLineRequest(productId.value, quantity = 2)

    fun allExist(ids: List<ProductId>) = VerifyProductsResponse(ids.toSet(), emptySet()).right()
    fun inventoryOk() = ReserveInventoryResponse(emptyList()).right()

    afterEach { clearAllMocks() }

    "returns InvalidOrderLines error when order lines are empty" {
        val result = sut.execute(CreateOrderRequest(emptyList()))

        result.leftOrNull().shouldBeInstanceOf<OrderError.InvalidOrderLines>()
    }

    "returns ProductsNotAvailable when a product does not exist" {
        val missingId = ProductId.generate()
        every { verifyProductExistUsecase.execute(any()) } returns
                VerifyProductsResponse(emptySet(), setOf(missingId)).right()

        val result = sut.execute(CreateOrderRequest(listOf(OrderLineRequest(missingId.value, 1))))

        val error = result.leftOrNull().shouldBeInstanceOf<OrderError.ProductsNotAvailable>()
        error.missingProducts shouldBe setOf(missingId)
    }

    "returns InsufficientStock when inventory cannot fulfill the order" {
        every { verifyProductExistUsecase.execute(any()) } returns allExist(listOf(productId))
        every { reserveInventoryUsecase.execute(any()) } returns
                InventoryError.InsufficientInventory(productId.value, available = 1, requested = 2).left()

        val result = sut.execute(CreateOrderRequest(listOf(orderLine)))

        result.leftOrNull().shouldBeInstanceOf<OrderError.InsufficientStock>()
    }

    "returns PaymentCreationFailed when the payment processor fails" {
        every { verifyProductExistUsecase.execute(any()) } returns allExist(listOf(productId))
        every { reserveInventoryUsecase.execute(any()) } returns inventoryOk()
        every { productRepository.findAllByIdIn(any()) } returns listOf(product)
        every { jdbcTemplate.insert(any<Order>()) } answers { firstArg() }
        paymentProcessor.result = PaymentError.ProcessorError("Stripe unavailable").left()

        val result = sut.execute(CreateOrderRequest(listOf(orderLine)))

        result.leftOrNull().shouldBeInstanceOf<OrderError.PaymentCreationFailed>()
    }

    "creates the order and returns the payment URL on success" {
        every { verifyProductExistUsecase.execute(any()) } returns allExist(listOf(productId))
        every { reserveInventoryUsecase.execute(any()) } returns inventoryOk()
        every { productRepository.findAllByIdIn(any()) } returns listOf(product)
        every { jdbcTemplate.insert(any<Order>()) } answers { firstArg() }
        paymentProcessor.result = PaymentLinkResult("https://pay.stripe.com/sess_123", "sess_123").right()

        val response = sut.execute(CreateOrderRequest(listOf(orderLine))).getOrNull()!!

        response.paymentUrl shouldBe "https://pay.stripe.com/sess_123"
        response.status shouldBe "PENDING"
        response.totalAmount shouldBe BigDecimal("20.00")
        response.currency shouldBe "USD"
        response.orderLines.size shouldBe 1
    }

    "calculates total correctly for multiple order lines" {
        val productId2 = ProductId.generate()
        val product2 = Product(id = productId2, name = "Gadget", description = null, price = Price(BigDecimal("5.00")))

        every { verifyProductExistUsecase.execute(any()) } returns allExist(listOf(productId, productId2))
        every { reserveInventoryUsecase.execute(any()) } returns inventoryOk()
        every { productRepository.findAllByIdIn(any()) } returns listOf(product, product2)
        every { jdbcTemplate.insert(any<Order>()) } answers { firstArg() }
        paymentProcessor.result = PaymentLinkResult("https://pay.stripe.com/sess_456", "sess_456").right()

        val request = CreateOrderRequest(
            listOf(
                OrderLineRequest(productId.value, quantity = 3),
                OrderLineRequest(productId2.value, quantity = 4)
            )
        )

        val response = sut.execute(request).getOrNull()!!

        response.totalAmount shouldBe BigDecimal("50.00")
    }
})

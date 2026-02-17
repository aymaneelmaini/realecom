package com.aymanegeek.imedia.order.application.usecase.impl

import com.aymanegeek.imedia.common.vo.Price
import com.aymanegeek.imedia.common.vo.ProductId
import com.aymanegeek.imedia.common.vo.Quantity
import com.aymanegeek.imedia.order.application.usecase.UpdateOrderPaymentStatusRequest
import com.aymanegeek.imedia.order.domain.Order
import com.aymanegeek.imedia.order.domain.OrderError
import com.aymanegeek.imedia.order.domain.OrderId
import com.aymanegeek.imedia.order.domain.OrderLine
import com.aymanegeek.imedia.order.domain.OrderLineId
import com.aymanegeek.imedia.order.domain.OrderRepository
import com.aymanegeek.imedia.order.domain.OrderStatus
import com.aymanegeek.imedia.payment.domain.Payment
import com.aymanegeek.imedia.payment.domain.PaymentRepository
import com.aymanegeek.imedia.payment.domain.PaymentStatus
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.math.BigDecimal
import java.util.Optional

class DefaultUpdateOrderPaymentStatusUseCaseTest : StringSpec({

    val orderRepository: OrderRepository = mockk()
    val paymentRepository: PaymentRepository = mockk()
    val sut = DefaultUpdateOrderPaymentStatusUseCase(orderRepository, paymentRepository)

    val orderId = OrderId.generate()
    val orderLine = OrderLine(
        id = OrderLineId.generate(),
        orderId = orderId,
        productId = ProductId.generate(),
        quantity = Quantity(1),
        price = Price(BigDecimal("25.00"))
    )
    val order = Order.create(orderId, mutableSetOf(orderLine))

    fun stubRepositorySaves() {
        every { orderRepository.save(any()) } answers { firstArg() }
        every { paymentRepository.save(any()) } answers { firstArg() }
    }

    afterEach { clearAllMocks() }

    "returns OrderNotFound when the order does not exist" {
        every { orderRepository.findById(orderId) } returns Optional.empty()

        val result = sut.execute(UpdateOrderPaymentStatusRequest(orderId, isPaid = true, externalPaymentId = "sess_1"))

        result.leftOrNull().shouldBeInstanceOf<OrderError.OrderNotFound>()
    }

    "marks the order as PAID and creates a SUCCESS payment when isPaid=true" {
        every { orderRepository.findById(orderId) } returns Optional.of(order)
        every { paymentRepository.findByOrderId(orderId) } returns null
        stubRepositorySaves()

        val response = sut.execute(UpdateOrderPaymentStatusRequest(orderId, isPaid = true, externalPaymentId = "sess_1")).getOrNull()!!

        response.status shouldBe "PAID"
        verify { orderRepository.save(match { it.status == OrderStatus.PAID }) }
        verify { paymentRepository.save(match { it.status == PaymentStatus.SUCCESS && it.externalPaymentId == "sess_1" }) }
    }

    "marks the order as FAILED and creates a FAILED payment when isPaid=false" {
        every { orderRepository.findById(orderId) } returns Optional.of(order)
        every { paymentRepository.findByOrderId(orderId) } returns null
        stubRepositorySaves()

        val response = sut.execute(UpdateOrderPaymentStatusRequest(orderId, isPaid = false, externalPaymentId = "sess_2")).getOrNull()!!

        response.status shouldBe "FAILED"
        verify { orderRepository.save(match { it.status == OrderStatus.FAILED }) }
        verify { paymentRepository.save(match { it.status == PaymentStatus.FAILED }) }
    }

    "updates the existing payment instead of creating a new one" {
        val existingPayment = Payment(
            orderId = orderId,
            amount = Price(BigDecimal("49.99")),
            status = PaymentStatus.PENDING,
            externalPaymentId = "old_sess"
        )
        every { orderRepository.findById(orderId) } returns Optional.of(order)
        every { paymentRepository.findByOrderId(orderId) } returns existingPayment
        stubRepositorySaves()

        sut.execute(UpdateOrderPaymentStatusRequest(orderId, isPaid = true, externalPaymentId = "new_sess"))

        verify {
            paymentRepository.save(match { it.status == PaymentStatus.SUCCESS && it.externalPaymentId == "new_sess" })
        }
    }

    "returns the orderId in the response" {
        every { orderRepository.findById(orderId) } returns Optional.of(order)
        every { paymentRepository.findByOrderId(orderId) } returns null
        stubRepositorySaves()

        val response = sut.execute(UpdateOrderPaymentStatusRequest(orderId, isPaid = true, externalPaymentId = "sess_3")).getOrNull()!!

        response.orderId shouldBe orderId
    }
})

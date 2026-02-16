package com.aymanegeek.imedia.order.application.usecase.impl

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.aymanegeek.imedia.common.vo.Quantity
import com.aymanegeek.imedia.inventory.application.usecase.InventoryItem
import com.aymanegeek.imedia.inventory.application.usecase.ReserveInventoryUsecase
import com.aymanegeek.imedia.inventory.domain.InventoryError
import com.aymanegeek.imedia.order.application.dto.CreateOrderRequest
import com.aymanegeek.imedia.order.application.dto.CreateOrderResponse
import com.aymanegeek.imedia.order.application.dto.OrderLineRequest
import com.aymanegeek.imedia.order.application.dto.OrderLineResponse
import com.aymanegeek.imedia.order.application.usecase.CreateOrderUseCase
import com.aymanegeek.imedia.order.domain.*
import com.aymanegeek.imedia.payment.domain.PaymentLineItem
import com.aymanegeek.imedia.payment.domain.PaymentProcessor
import com.aymanegeek.imedia.product.application.usecase.VerifyProductsExistUsecase
import com.aymanegeek.imedia.product.application.usecase.VerifyProductsRequest
import com.aymanegeek.imedia.product.application.usecase.VerifyProductsResponse
import com.aymanegeek.imedia.product.domain.ProductError
import com.aymanegeek.imedia.product.domain.ProductId
import com.aymanegeek.imedia.product.domain.ProductRepository
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DefaultCreateOrderUseCase(
    private val verifyProductExistUsecase: VerifyProductsExistUsecase,
    private val reserveInventoryUsecase: ReserveInventoryUsecase,
    private val productRepository: ProductRepository,
    private val paymentProcessor: PaymentProcessor,
    private val jdbcTemplate: JdbcAggregateTemplate
) : CreateOrderUseCase {

    @Transactional
    override fun execute(request: CreateOrderRequest): Either<OrderError, CreateOrderResponse> = either {
        val (orderLines, paymentMethod) = request

        verifyProductAvailability(orderLines)

        val inventoryItems = orderLines.map {
            InventoryItem(
                ProductId(it.productId),
                Quantity(it.quantity)
            )
        }

        reserveInventoryUsecase.execute(inventoryItems).mapLeft { inventoryError ->
            when (inventoryError) {
                is InventoryError.InsufficientInventory -> OrderError.InsufficientStock(
                    listOf(
                        InsufficientStockItem(
                            productId = inventoryError.productId,
                            requested = inventoryError.requested,
                            available = inventoryError.available
                        )
                    )
                )
                else -> OrderError.InvalidOrderLines("Inventory error: ${inventoryError.message}")
            }
        }.bind()

        val productIds = orderLines.map { ProductId(it.productId) }
        val products = productRepository.findAllByIdIn(productIds.map { it.value })
        val productMap = products.associateBy { it.id }

        val orderId = OrderId.generate()
        val orderLineEntities = orderLines.map { it ->
            val product = productMap[ProductId(it.productId)]!!
            OrderLine(
                id = OrderLineId.generate(),
                orderId = orderId,
                productId = ProductId(it.productId),
                quantity = Quantity(it.quantity),
                price = product.price,
                createdAt = null
            )
        }

        val order = jdbcTemplate.insert(
            Order.create(orderId, orderLineEntities.toMutableSet())
        )

        val paymentLineItems = orderLineEntities.map { line ->
            val product = productMap[line.productId]!!
            PaymentLineItem(
                name = product.name,
                unitPrice = line.price,
                quantity = line.quantity.value
            )
        }

        val paymentLink = paymentProcessor.createPaymentLink(
            orderId = order.id,
            lineItems = paymentLineItems,
            description = "Order #${order.id.value}"
        ).mapLeft { paymentError ->
            OrderError.PaymentCreationFailed(paymentError.message)
        }.bind()

        CreateOrderResponse(
            orderId = order.id.value,
            status = order.status.name,
            totalAmount = order.totalPrice.amount,
            currency = order.totalPrice.currency,
            paymentUrl = paymentLink.paymentUrl,
            orderLines = orderLineEntities.map { line ->
                OrderLineResponse(
                    productId = line.productId.value,
                    quantity = line.quantity.value,
                    unitPrice = line.price.amount,
                    totalPrice = line.price.amount * line.quantity.value.toBigDecimal()
                )
            }
        )
    }

    private fun Raise<OrderError>.verifyProductAvailability(orderLines: List<OrderLineRequest>) {
        ensure(orderLines.isNotEmpty()) {
            OrderError.InvalidOrderLines("Order must have at least 1 order")
        }

        val productIds = orderLines.map { ProductId(it.productId) }
        val productVerification: VerifyProductsResponse = verifyProductExistUsecase.execute(
            VerifyProductsRequest(productIds)
        ).mapLeft { productError ->
            when (productError) {
                is ProductError.ProductNotFound -> OrderError.ProductsNotAvailable(setOf(ProductId(productError.productId)))
                else -> OrderError.InvalidOrderLines("Product error: ${productError.message}")
            }
        }.bind()

        ensure(productVerification.allExist) {
            OrderError.ProductsNotAvailable(productVerification.missingProducts)
        }
    }

}
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
import com.aymanegeek.imedia.product.domain.ProductError
import com.aymanegeek.imedia.common.vo.ProductId
import com.aymanegeek.imedia.product.domain.ProductRepository
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.transaction.annotation.Transactional

@Transactional
class DefaultCreateOrderUseCase(
    private val verifyProductExistUsecase: VerifyProductsExistUsecase,
    private val reserveInventoryUsecase: ReserveInventoryUsecase,
    private val productRepository: ProductRepository,
    private val paymentProcessor: PaymentProcessor,
    private val jdbcTemplate: JdbcAggregateTemplate
) : CreateOrderUseCase {

    override fun execute(request: CreateOrderRequest): Either<OrderError, CreateOrderResponse> = either {
        val (orderLines, _) = request

        verifyProductAvailability(orderLines)
        reserveInventory(orderLines)

        val productMap = fetchProducts(orderLines)
        val orderId = OrderId.generate()
        val orderLineEntities = createOrderLineEntities(orderId, orderLines, productMap)

        val order = jdbcTemplate.insert(Order.create(orderId, orderLineEntities.toMutableSet()))

        val paymentLink = createPaymentLink(order, orderLineEntities, productMap)

        buildResponse(order, orderLineEntities, paymentLink.paymentUrl)
    }

    private fun Raise<OrderError>.verifyProductAvailability(orderLines: List<OrderLineRequest>) {
        ensure(orderLines.isNotEmpty()) {
            OrderError.InvalidOrderLines("Order must have at least 1 order")
        }

        val productIds = orderLines.map(::toProductId)
        val productVerification = verifyProductExistUsecase.execute(VerifyProductsRequest(productIds))
            .mapLeft(::mapProductError)
            .bind()

        ensure(productVerification.allExist) {
            OrderError.ProductsNotAvailable(productVerification.missingProducts)
        }
    }

    private fun Raise<OrderError>.reserveInventory(orderLines: List<OrderLineRequest>) {
        val inventoryItems = orderLines.map(::toInventoryItem)
        reserveInventoryUsecase.execute(inventoryItems)
            .mapLeft(::mapInventoryError)
            .bind()
    }

    private fun fetchProducts(orderLines: List<OrderLineRequest>) =
        orderLines
            .map(::toProductId)
            .map(ProductId::value)
            .let(productRepository::findAllByIdIn)
            .associateBy { it.id }

    private fun createOrderLineEntities(
        orderId: OrderId,
        orderLines: List<OrderLineRequest>,
        productMap: Map<ProductId, com.aymanegeek.imedia.product.domain.Product>
    ) = orderLines.map { request ->
        val productId = ProductId(request.productId)
        val product = productMap.getValue(productId)
        OrderLine(
            id = OrderLineId.generate(),
            orderId = orderId,
            productId = productId,
            quantity = Quantity(request.quantity),
            price = product.price,
            createdAt = null
        )
    }

    private fun Raise<OrderError>.createPaymentLink(
        order: Order,
        orderLines: List<OrderLine>,
        productMap: Map<ProductId, com.aymanegeek.imedia.product.domain.Product>
    ) = paymentProcessor.createPaymentLink(
        orderId = order.id,
        lineItems = orderLines.map { toPaymentLineItem(it, productMap) },
        description = "Order #${order.id.value}"
    ).mapLeft { OrderError.PaymentCreationFailed(it.message) }
        .bind()

    private fun buildResponse(
        order: Order,
        orderLines: List<OrderLine>,
        paymentUrl: String
    ) = CreateOrderResponse(
        orderId = order.id.value,
        status = order.status.name,
        totalAmount = order.totalPrice.amount,
        currency = order.totalPrice.currency,
        paymentUrl = paymentUrl,
        orderLines = orderLines.map(::toOrderLineResponse)
    )

    private fun toProductId(request: OrderLineRequest) = ProductId(request.productId)

    private fun toInventoryItem(request: OrderLineRequest) = InventoryItem(
        ProductId(request.productId),
        Quantity(request.quantity)
    )

    private fun toPaymentLineItem(
        line: OrderLine,
        productMap: Map<ProductId, com.aymanegeek.imedia.product.domain.Product>
    ) = PaymentLineItem(
        name = productMap.getValue(line.productId).name,
        unitPrice = line.price,
        quantity = line.quantity.value
    )

    private fun toOrderLineResponse(line: OrderLine) = OrderLineResponse(
        productId = line.productId.value,
        quantity = line.quantity.value,
        unitPrice = line.price.amount,
        totalPrice = line.price.amount * line.quantity.value.toBigDecimal()
    )

    private fun mapProductError(error: ProductError) = when (error) {
        is ProductError.ProductNotFound -> OrderError.ProductsNotAvailable(setOf(ProductId(error.productId)))
        else -> OrderError.InvalidOrderLines("Product error: ${error.message}")
    }

    private fun mapInventoryError(error: InventoryError) = when (error) {
        is InventoryError.InsufficientInventory -> OrderError.InsufficientStock(
            listOf(
                InsufficientStockItem(
                    productId = error.productId,
                    requested = error.requested,
                    available = error.available
                )
            )
        )

        else -> OrderError.InvalidOrderLines("Inventory error: ${error.message}")
    }

}
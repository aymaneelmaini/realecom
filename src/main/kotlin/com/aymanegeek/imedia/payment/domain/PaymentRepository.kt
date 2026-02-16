package com.aymanegeek.imedia.payment.domain

import com.aymanegeek.imedia.order.domain.OrderId
import org.springframework.data.repository.CrudRepository
import java.util.*

interface PaymentRepository : CrudRepository<Payment, UUID> {
    fun findByOrderId(orderId: OrderId): Payment?
}
package com.newonedev.easymart.payment.domain

import com.newonedev.easymart.order.domain.OrderId
import org.springframework.data.repository.CrudRepository
import java.util.*

interface PaymentRepository : CrudRepository<Payment, UUID> {
    fun findByOrderId(orderId: OrderId): Payment?
}
package com.newonedev.easymart.order.domain

import org.springframework.data.repository.CrudRepository

interface OrderRepository : CrudRepository<Order, OrderId>
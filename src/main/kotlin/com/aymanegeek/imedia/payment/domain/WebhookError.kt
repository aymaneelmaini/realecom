package com.aymanegeek.imedia.payment.domain

sealed class WebhookError(val message: String) {
    data object InvalidSignature : WebhookError("Invalid webhook signature")
    data class InvalidEventData(val reason: String) : WebhookError(reason)
    data class OrderUpdateFailed(val reason: String) : WebhookError(reason)
}

package com.vasiliytemniy.deliverymicroservice.dto

import java.time.LocalDateTime
import javax.validation.constraints.Size

data class CreateOrderTrackingDto (
    val orderId: Long,
    val fromFacilityId: Long,
    val destinationId: Long,
    val destinationType: String,
    val carrierId: Long,
    val status: String,
    val deliveryCost: Int,
    val currency: String,
    val massControlValue: Int?,
    val massMeasure: String?,
    val estimatedDeliveryAt: String?,
    val deliveredAt: String?
)
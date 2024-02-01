package com.vasiliytemniy.deliverymicroservice.dto

import java.util.*

data class SuccessOrderTrackingResponse(
    val id: UUID?,
    val orderId: String?,
    val pointNumber: Int?,
    val fromFacilityId: String?,
    val destinationId: String?,
    val destinationType: String?,
    val carrierId: String?,
    val status: String?,
    val deliveryCost: Int?,
    val currency: String?,
    val currencyDecimalMultiplier: Int?,
    val massControlValue: Int?,
    val massMeasure: String?,
    val estimatedDeliveryAt: String?,
    val deliveredAt: String?,
    val createdAt: String?,
    val updatedAt: String?
)
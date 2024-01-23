package com.vasiliytemniy.deliverymicroservice.dto

import java.util.*

data class SuccessOrderTrackingResponse(
    val id: UUID?,
    val orderId: Long?,
    val pointNumber: Int?,
    val fromFacilityId: Long?,
    val destinationId: Long?,
    val destinationType: String?,
    val carrierId: Long?,
    val status: String?,
    val deliveryCost: Int?,
    val currency: String?,
    val massControlValue: Int?,
    val massMeasure: String?,
    val estimatedDeliveryAt: String?,
    val deliveredAt: String?,
    val createdAt: String?,
    val updatedAt: String?
)
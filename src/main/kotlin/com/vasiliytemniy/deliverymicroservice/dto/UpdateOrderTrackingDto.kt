package com.vasiliytemniy.deliverymicroservice.dto


data class UpdateOrderTrackingDto (
    val orderId: Long,
    val pointNumber: Int,
    val fromFacilityId: Long,
    val destinationId: Long,
    val destinationType: String,
    val carrierId: Long,
    val status: String,
    val deliveryCost: Int,
    val currency: String,
    val currencyDecimalMultiplier: Int,
    val massControlValue: Int?,
    val massMeasure: String?,
    val estimatedDeliveryAt: String?,
    val deliveredAt: String?
)
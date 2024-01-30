package com.vasiliytemniy.deliverymicroservice.dto


data class CreateOrderTrackingDto (
    val orderId: String,
    val pointNumber: Int?,
    val fromFacilityId: String,
    val destinationId: String,
    val destinationType: String,
    val carrierId: String,
    val status: String,
    val deliveryCost: Int,
    val currency: String,
    val currencyDecimalMultiplier: Int,
    val massControlValue: Int?,
    val massMeasure: String?,
    val lat: Double?,
    val lon: Double?,
    val estimatedDeliveryAt: String?,
    val deliveredAt: String?
)
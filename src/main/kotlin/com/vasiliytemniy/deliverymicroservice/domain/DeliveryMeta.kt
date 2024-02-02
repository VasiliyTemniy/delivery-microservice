package com.vasiliytemniy.deliverymicroservice.domain

import com.vasiliytemniy.deliverymicroservice.dto.SuccessDeliveryMetaResponse
import com.vasiliytemniy.grpc.deliverymeta.service.DeliveryMeta.DeliveryMetaData
import java.time.LocalDateTime

data class DeliveryMeta(
    val cost: Int?,
    val expectedDeliveryAt: LocalDateTime?
)

fun DeliveryMeta.toProto(): DeliveryMetaData {
    return DeliveryMetaData.newBuilder()
        .setCost(cost?:0)
        .setExpectedDeliveryAt(expectedDeliveryAt?.toString())
        .build()
}

fun DeliveryMeta.toSuccessHttpResponse() = SuccessDeliveryMetaResponse(
    cost = cost,
    expectedDeliveryAt = expectedDeliveryAt?.toString()
)
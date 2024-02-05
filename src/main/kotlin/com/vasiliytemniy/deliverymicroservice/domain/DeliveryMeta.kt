package com.vasiliytemniy.deliverymicroservice.domain

import com.vasiliytemniy.deliverymicroservice.dto.SuccessDeliveryMetaResponse
import com.vasiliytemniy.grpc.deliverymeta.service.DeliveryMeta.DeliveryMetaData

data class DeliveryMeta(
    val cost: Int?,
    val estimatedDeliveryMs: Long?
)

fun DeliveryMeta.toProto(): DeliveryMetaData {
    return DeliveryMetaData.newBuilder()
        .setCost(cost?:0)
        .setEstimatedDeliveryMs(estimatedDeliveryMs?:0)
        .build()
}

fun DeliveryMeta.toSuccessHttpResponse() = SuccessDeliveryMetaResponse(
    cost = cost,
    estimatedDeliveryMs = estimatedDeliveryMs
)
package com.vasiliytemniy.deliverymicroservice.dto

import com.vasiliytemniy.deliverymicroservice.utils.parseOptionalDate
import com.vasiliytemniy.grpc.ordertracking.service.OrderTracking
import java.util.LinkedHashMap

data class SetOrderTrackingStatusesDto(
    val orderTrackingExternalIds: List<OrderTrackingExternalIdDto>,
    val status: String,
    val deliveredAt: String?,
) {

    val parsedDeliveredAt = parseOptionalDate(deliveredAt)

    init {
        require(orderTrackingExternalIds.isNotEmpty()) { "orderTrackingExternalIds must not be empty" }
    }

    companion object
}


fun SetOrderTrackingStatusesDto.Companion.of(request: OrderTracking.SetStatusesRequest): SetOrderTrackingStatusesDto {

    val orderTrackingExternalIds = request.orderTrackingExternalIdsList.toList()
        .map { OrderTrackingExternalIdDto.of(it) }

    return SetOrderTrackingStatusesDto(orderTrackingExternalIds, request.status, request.deliveredAt)
}


fun SetOrderTrackingStatusesDto.Companion.of(request: Any): SetOrderTrackingStatusesDto {
    if (request !is LinkedHashMap<*, *>)
        throw IllegalArgumentException("Invalid request")

    if (
        request.keys.size != 3
        || !request.keys.containsAll(listOf("orderTrackingExternalIds", "status", "deliveredAt"))
        || request["orderTrackingExternalIds"] !is List<*>
        || request["status"] !is String
        || !(request["deliveredAt"] is String || request["deliveredAt"] == null)
    )
        throw IllegalArgumentException("Invalid request: invalid request body")


    val orderTrackingExternalIds = request["orderTrackingExternalIds"] as List<*>

    if (orderTrackingExternalIds.isEmpty())
        throw IllegalArgumentException("Invalid request: orderTrackingExternalIds must not be empty")

    val parsedOrderTrackingExternalIds = mutableListOf<OrderTrackingExternalIdDto>()

    orderTrackingExternalIds.forEach {
        if (
            it !is LinkedHashMap<*, *>
            || !it.keys.containsAll(listOf("orderId", "pointNumber"))
            || !(it["orderId"] is String || it["orderId"] is Int)
            || it["pointNumber"] !is Int
        )
            throw IllegalArgumentException("Invalid request: invalid orderTrackingExternalIds")

        val stringifiedOrderId =
            if (it["orderId"] is String)
                it["orderId"] as String
            else (it["orderId"] as Int).toString()

        parsedOrderTrackingExternalIds.add(OrderTrackingExternalIdDto(
            stringifiedOrderId,
            it["pointNumber"] as Int
        ))
    }

    return SetOrderTrackingStatusesDto(
        parsedOrderTrackingExternalIds,
        request["status"] as String,
        request["deliveredAt"] as String,
    )
}
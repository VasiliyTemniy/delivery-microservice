package com.vasiliytemniy.deliverymicroservice.dto

import com.vasiliytemniy.grpc.delivery.service.Delivery
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable


data class GetOrderTrackingsByOrderIdDto(
    val orderId: Long,
    val page: Int,
    val size: Int,
    val findLast: Boolean,
    val pageable: Pageable
) {
    companion object
}


fun GetOrderTrackingsByOrderIdDto.Companion.of(request: Delivery.GetOrderTrackingsByOrderIdRequest): GetOrderTrackingsByOrderIdDto {
    return GetOrderTrackingsByOrderIdDto(request.orderId, request.page, request.size, request.findLast, PageRequest.of(request.page, request.size))
}
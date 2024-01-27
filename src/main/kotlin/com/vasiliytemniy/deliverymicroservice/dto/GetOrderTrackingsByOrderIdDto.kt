package com.vasiliytemniy.deliverymicroservice.dto

import com.vasiliytemniy.grpc.ordertracking.service.OrderTracking
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable


data class GetOrderTrackingsByOrderIdDto(
    val orderId: Long,
    val pageable: Pageable
) {
    companion object
}


fun GetOrderTrackingsByOrderIdDto.Companion.of(request: OrderTracking.GetPageByOrderIdRequest): GetOrderTrackingsByOrderIdDto {
    return GetOrderTrackingsByOrderIdDto(request.orderId, PageRequest.of(request.page, request.size))
}
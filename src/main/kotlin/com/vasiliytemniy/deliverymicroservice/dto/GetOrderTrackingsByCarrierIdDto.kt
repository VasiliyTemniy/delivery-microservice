package com.vasiliytemniy.deliverymicroservice.dto

import com.vasiliytemniy.grpc.ordertracking.service.OrderTracking
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable


data class GetOrderTrackingsByCarrierIdDto(
    val carrierId: Long,
    val pageable: Pageable
) {
    companion object
}


fun GetOrderTrackingsByCarrierIdDto.Companion.of(request: OrderTracking.GetOrderTrackingsByCarrierIdRequest): GetOrderTrackingsByCarrierIdDto {
    return GetOrderTrackingsByCarrierIdDto(request.carrierId, PageRequest.of(request.page, request.size))
}
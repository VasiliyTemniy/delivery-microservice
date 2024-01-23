package com.vasiliytemniy.deliverymicroservice.dto

import com.vasiliytemniy.grpc.delivery.service.Delivery
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable


data class GetOrderTrackingsByCarrierIdDto(
    val carrierId: Long,
    val pageable: Pageable
) {
    companion object
}


fun GetOrderTrackingsByCarrierIdDto.Companion.of(request: Delivery.GetOrderTrackingsByCarrierIdRequest): GetOrderTrackingsByCarrierIdDto {
    return GetOrderTrackingsByCarrierIdDto(request.carrierId, PageRequest.of(request.page, request.size))
}
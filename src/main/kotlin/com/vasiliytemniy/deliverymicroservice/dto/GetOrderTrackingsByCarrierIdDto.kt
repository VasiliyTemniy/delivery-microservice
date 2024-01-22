package com.vasiliytemniy.deliverymicroservice.dto

import com.vasiliytemniy.grpc.delivery.service.Delivery
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable


data class GetOrderTrackingsByCarrierIdDto(
    val carrierId: Long,
    val page: Int,
    val size: Int,
    val filterActive: Boolean,
    val pageable: Pageable
) {
    companion object
}


fun GetOrderTrackingsByCarrierIdDto.Companion.of(request: Delivery.GetOrderTrackingsByCarrierIdRequest): GetOrderTrackingsByCarrierIdDto {
    return GetOrderTrackingsByCarrierIdDto(request.carrierId, request.page, request.size, request.filterActive, PageRequest.of(request.page, request.size))
}
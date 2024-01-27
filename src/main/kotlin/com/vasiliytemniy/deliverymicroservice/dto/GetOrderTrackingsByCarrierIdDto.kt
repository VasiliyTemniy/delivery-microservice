package com.vasiliytemniy.deliverymicroservice.dto

import com.vasiliytemniy.grpc.ordertracking.service.OrderTracking
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable


data class GetOrderTrackingsByCarrierIdDto(
    val carrierId: Long,
    val pageable: Pageable,
    val filterActive: Boolean
) {
    companion object
}


fun GetOrderTrackingsByCarrierIdDto.Companion.of(request: OrderTracking.GetPageByCarrierIdRequest): GetOrderTrackingsByCarrierIdDto {
    return GetOrderTrackingsByCarrierIdDto(request.carrierId, PageRequest.of(request.page, request.size), request.filterActive)
}
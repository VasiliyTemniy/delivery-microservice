package com.vasiliytemniy.deliverymicroservice.services

import org.springframework.data.domain.Page
import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import com.vasiliytemniy.deliverymicroservice.dto.GetOrderTrackingsByOrderIdDto
import com.vasiliytemniy.deliverymicroservice.dto.GetOrderTrackingsByCarrierIdDto
import com.vasiliytemniy.deliverymicroservice.dto.SetOrderTrackingStatusesDto
import com.vasiliytemniy.deliverymicroservice.dto.UpdateOrderTrackingDto
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Service
interface OrderTrackingReactiveService {

    fun createOrderTracking(orderTracking: OrderTracking): Mono<OrderTracking>

    fun getOrderTrackingsByOrderId(requestDto: GetOrderTrackingsByOrderIdDto): Mono<Page<OrderTracking>>

    fun getLastOrderTrackingByOrderId(orderId: Long): Mono<OrderTracking>?

    fun getOrderTrackingsByCarrierId(requestDto: GetOrderTrackingsByCarrierIdDto, active: Boolean): Mono<Page<OrderTracking>>

    fun setOrderTrackingStatuses(requestDto: SetOrderTrackingStatusesDto): Flux<OrderTracking>

    fun updateOrderTracking(requestDto: UpdateOrderTrackingDto): Mono<OrderTracking>

}
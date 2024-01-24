package com.vasiliytemniy.deliverymicroservice.services

import org.springframework.data.domain.Page
import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import com.vasiliytemniy.deliverymicroservice.dto.GetOrderTrackingsByOrderIdDto
import com.vasiliytemniy.deliverymicroservice.dto.GetOrderTrackingsByCarrierIdDto
import com.vasiliytemniy.deliverymicroservice.dto.SetOrderTrackingStatusDto
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono


@Service
interface OrderTrackingService {

    suspend fun createOrderTracking(orderTracking: OrderTracking): OrderTracking

    fun getOrderTrackingsByOrderId(requestDto: GetOrderTrackingsByOrderIdDto): Mono<Page<OrderTracking>>

    fun getOrderTrackingsByCarrierId(requestDto: GetOrderTrackingsByCarrierIdDto): Mono<Page<OrderTracking>>

    fun getOrderTrackingsByOrderIdFlow(requestDto: GetOrderTrackingsByOrderIdDto): Flow<OrderTracking>

    fun getOrderTrackingsByCarrierIdFlow(requestDto: GetOrderTrackingsByCarrierIdDto): Flow<OrderTracking>

    fun getLastOrderTrackingByOrderId(orderId: Long): Mono<OrderTracking>?

    fun getActiveOrderTrackingsByCarrierId(requestDto: GetOrderTrackingsByCarrierIdDto): Mono<Page<OrderTracking>>

    fun setOrderTrackingStatus(requestDto: SetOrderTrackingStatusDto): Mono<OrderTracking>

}
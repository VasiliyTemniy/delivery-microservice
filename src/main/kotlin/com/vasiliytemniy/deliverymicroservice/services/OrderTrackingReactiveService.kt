package com.vasiliytemniy.deliverymicroservice.services

import org.springframework.data.domain.Page
import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import com.vasiliytemniy.deliverymicroservice.dto.*
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Service
interface OrderTrackingReactiveService {

    fun create(orderTracking: OrderTracking): Mono<OrderTracking>

    fun getPageByOrderId(requestDto: GetOrderTrackingsByOrderIdDto): Mono<Page<OrderTracking>>

    fun getAllByOrderId(orderId: Long): Flux<OrderTracking>

    fun getLastByOrderId(orderId: Long): Mono<OrderTracking>

    fun getPageByCarrierId(requestDto: GetOrderTrackingsByCarrierIdDto): Mono<Page<OrderTracking>>

    fun getAllByCarrierId(carrierId: Long, filterActive: Boolean): Flux<OrderTracking>

    fun setStatuses(requestDto: SetOrderTrackingStatusesDto): Flux<OrderTracking>

    fun reorder(requestDto: ReorderOrderTrackingsDto): Flux<OrderTracking>

    fun update(requestDto: UpdateOrderTrackingDto): Mono<OrderTracking>

    fun deleteAllByOrderId(orderId: Long): Flux<OrderTracking>

    fun deleteByExternalId(orderId: Long, pointNumber: Int): Mono<OrderTracking>

}
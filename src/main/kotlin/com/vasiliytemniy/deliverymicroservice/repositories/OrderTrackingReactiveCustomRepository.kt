package com.vasiliytemniy.deliverymicroservice.repositories

import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Repository
interface OrderTrackingReactiveCustomRepository {

    fun findPageByCarrierId(carrierId: String, pageable: Pageable, filterActive: Boolean): Flux<OrderTracking>

    fun findAllByCarrierId(carrierId: String, filterActive: Boolean): Flux<OrderTracking>

    fun setPointNumber(orderId: String, fromPointNumber: Int, toPointNumber: Int): Mono<OrderTracking>

    fun countByOrderId(orderId: String): Mono<Long>

    fun countByCarrierId(carrierId: String, filterActive: Boolean): Mono<Long>

}
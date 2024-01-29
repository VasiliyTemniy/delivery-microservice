package com.vasiliytemniy.deliverymicroservice.repositories

import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono


@Repository
interface OrderTrackingReactiveCustomRepository {

    fun setPointNumber(orderId: String, fromPointNumber: Int, toPointNumber: Int): Mono<OrderTracking>

}
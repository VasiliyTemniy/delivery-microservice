package com.vasiliytemniy.deliverymicroservice.repositories

import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Repository
interface OrderTrackingReactiveRepository: ReactiveCrudRepository<OrderTracking, Long> {

//    @Query("SELECT * FROM order_trackings WHERE order_id = :orderId")
    fun findByOrderId(orderId: Long, pageable: Pageable): Flux<OrderTracking>

//    @Query("SELECT * FROM order_trackings WHERE carrier_id = :carrierId")
    fun findByCarrierId(carrierId: Long, pageable: Pageable): Flux<OrderTracking>

    @Query("SELECT * FROM order_trackings WHERE order_id = :orderId ORDER BY point_number DESC LIMIT 1")
    fun findLastByOrderId(orderId: Long): Mono<OrderTracking>

    @Query("SELECT * FROM order_trackings WHERE carrier_id = :carrierId AND delivered_at IS NULL")
    fun findActiveByCarrierId(carrierId: Long, pageable: Pageable): Flux<OrderTracking>

    @Query("UPDATE order_trackings SET status = :status, delivered_at = :deliveredAt WHERE order_id = :orderId, point_number = :pointNumber")
    fun setOrderTrackingStatus(orderId: Long, pointNumber: Int, status: String, deliveredAt: String?): Mono<OrderTracking>

}
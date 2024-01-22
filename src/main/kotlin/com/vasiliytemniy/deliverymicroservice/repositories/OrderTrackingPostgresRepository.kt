package com.vasiliytemniy.deliverymicroservice.repositories

import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux


@Repository
interface OrderTrackingPostgresRepository {

    @Query("SELECT * FROM order_tracking WHERE order_id = :orderId")
    suspend fun findByOrderId(orderId: Long, pageable: Pageable): Flux<OrderTracking>

    @Query("SELECT * FROM order_tracking WHERE carrier_id = :carrierId")
    suspend fun findByCarrierId(carrierId: Long, pageable: Pageable): Flux<OrderTracking>

//    suspend fun findByOrderId(orderId: Long, pageable: Pageable): Page<OrderTracking>
//
//    suspend fun findByCarrierId(carrierId: Long, pageable: Pageable): Page<OrderTracking>

}
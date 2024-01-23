package com.vasiliytemniy.deliverymicroservice.repositories

import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.stereotype.Repository


@Repository
interface OrderTrackingPostgresRepository {

    @Query("SELECT * FROM order_tracking WHERE order_id = :orderId")
    suspend fun findByOrderId(orderId: Long, pageable: Pageable): Page<OrderTracking>

    @Query("SELECT * FROM order_tracking WHERE carrier_id = :carrierId")
    suspend fun findByCarrierId(carrierId: Long, pageable: Pageable): Page<OrderTracking>

    @Query("SELECT * FROM order_tracking WHERE order_id = :orderId")
    fun findByOrderIdFlow(orderId: Long, pageable: Pageable): Flow<OrderTracking>

    @Query("SELECT * FROM order_tracking WHERE carrier_id = :carrierId")
    fun findByCarrierIdFlow(carrierId: Long, pageable: Pageable): Flow<OrderTracking>

    @Query("SELECT * FROM order_tracking WHERE order_id = :orderId ORDER BY point_number DESC LIMIT 1")
    suspend fun findLastByOrderId(orderId: Long): OrderTracking

    @Query("SELECT * FROM order_tracking WHERE carrier_id = :carrierId AND delivered_at IS NULL")
    suspend fun findActiveByCarrierId(carrierId: Long, pageable: Pageable): Page<OrderTracking>

    @Query("UPDATE order_tracking SET status = :status, delivered_at = :deliveredAt WHERE order_id = :orderId, point_number = :pointNumber")
    suspend fun setOrderTrackingStatus(orderId: Long, pointNumber: Int, status: String, deliveredAt: String?): OrderTracking

}
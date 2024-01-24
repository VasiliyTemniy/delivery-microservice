package com.vasiliytemniy.deliverymicroservice.repositories

import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository


@Repository
interface OrderTrackingCoroutineRepository: CoroutineCrudRepository<OrderTracking, Long> {

    @Query("SELECT * FROM order_trackings WHERE order_id = :orderId")
    fun findByOrderIdFlow(orderId: Long, pageable: Pageable): Flow<OrderTracking>

    @Query("SELECT * FROM order_trackings WHERE carrier_id = :carrierId")
    fun findByCarrierIdFlow(carrierId: Long, pageable: Pageable): Flow<OrderTracking>

}
package com.vasiliytemniy.deliverymicroservice.repositories

import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime


@Repository
interface OrderTrackingCoroutineCustomRepository {

    suspend fun findPageByOrderId(orderId: Long, pageable: Pageable): Page<OrderTracking>

    suspend fun findPageByCarrierId(carrierId: Long, pageable: Pageable, filterActive: Boolean): Page<OrderTracking>

    fun findAllByCarrierId(carrierId: Long, filterActive: Boolean): Flow<OrderTracking>

}
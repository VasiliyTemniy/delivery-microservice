package com.vasiliytemniy.deliverymicroservice.repositories

import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import com.vasiliytemniy.deliverymicroservice.repositories.SqlQueries.Companion.DELETE_ALL_ORDER_TRACKINGS_BY_ORDER_ID_SQL_QUERY
import com.vasiliytemniy.deliverymicroservice.repositories.SqlQueries.Companion.DELETE_ORDER_TRACKING_SQL_QUERY
import com.vasiliytemniy.deliverymicroservice.repositories.SqlQueries.Companion.FIND_ALL_BY_CARRIER_ID_SQL_QUERY
import com.vasiliytemniy.deliverymicroservice.repositories.SqlQueries.Companion.FIND_ALL_BY_ORDER_ID_SQL_QUERY
import com.vasiliytemniy.deliverymicroservice.repositories.SqlQueries.Companion.FIND_LAST_BY_ORDER_ID_SQL_QUERY
import com.vasiliytemniy.deliverymicroservice.repositories.SqlQueries.Companion.FIND_PAGE_ACTIVE_BY_CARRIER_ID_SQL_QUERY
import com.vasiliytemniy.deliverymicroservice.repositories.SqlQueries.Companion.FIND_PAGE_BY_CARRIER_ID_SQL_QUERY
import com.vasiliytemniy.deliverymicroservice.repositories.SqlQueries.Companion.FIND_PAGE_BY_ORDER_ID_SQL_QUERY
import com.vasiliytemniy.deliverymicroservice.repositories.SqlQueries.Companion.UPDATE_ORDER_TRACKING_SQL_QUERY
import com.vasiliytemniy.deliverymicroservice.repositories.SqlQueries.Companion.SET_ORDER_TRACKING_STATUS_SQL_QUERY
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository


@Repository
interface OrderTrackingCoroutineRepository : CoroutineCrudRepository<OrderTracking, Long> {

    @Query(FIND_PAGE_BY_ORDER_ID_SQL_QUERY)
    fun findPageByOrderId(orderId: Long, pageable: Pageable): Flow<OrderTracking>

    @Query(FIND_ALL_BY_ORDER_ID_SQL_QUERY)
    fun findAllByOrderId(orderId: Long): Flow<OrderTracking>

    @Query(FIND_LAST_BY_ORDER_ID_SQL_QUERY)
    suspend fun findLastByOrderId(orderId: Long): OrderTracking

    @Query(FIND_PAGE_BY_CARRIER_ID_SQL_QUERY)
    fun findPageByCarrierId(carrierId: Long, pageable: Pageable): Flow<OrderTracking>

    @Query(FIND_ALL_BY_CARRIER_ID_SQL_QUERY)
    fun findAllByCarrierId(carrierId: Long): Flow<OrderTracking>

    @Query(FIND_PAGE_ACTIVE_BY_CARRIER_ID_SQL_QUERY)
    fun findPageActiveByCarrierId(carrierId: Long, pageable: Pageable): Flow<OrderTracking>

    @Query(SET_ORDER_TRACKING_STATUS_SQL_QUERY)
    suspend fun setOrderTrackingStatus(
        orderId: Long,
        pointNumber: Int,
        status: String,
        deliveredAt: String?
    ): OrderTracking

    @Query(UPDATE_ORDER_TRACKING_SQL_QUERY)
    fun updateOrderTracking(
        orderId: Long,
        pointNumber: Int,
        fromFacilityId: Long,
        destinationId: Long,
        destinationType: String,
        carrierId: Long,
        status: String,
        deliveryCost: Int,
        currency: String,
        currencyDecimalMultiplier: Int,
        massControlValue: Int?,
        massMeasure: String?,
        estimatedDeliveryAt: String?,
        deliveredAt: String?
    ): OrderTracking

    @Query(DELETE_ALL_ORDER_TRACKINGS_BY_ORDER_ID_SQL_QUERY)
    suspend fun deleteAllByOrderId(orderId: Long): Unit

    @Query(DELETE_ORDER_TRACKING_SQL_QUERY)
    suspend fun deleteByOrderTrackingIdentifier(orderId: Long, pointNumber: Int): Unit

}
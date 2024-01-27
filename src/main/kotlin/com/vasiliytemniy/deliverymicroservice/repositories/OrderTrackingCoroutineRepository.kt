package com.vasiliytemniy.deliverymicroservice.repositories

import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime


@Repository
interface OrderTrackingCoroutineRepository : CoroutineCrudRepository<OrderTracking, Long>, OrderTrackingCoroutineCustomRepository {

    fun findAllByOrderId(orderId: Long): Flow<OrderTracking>

    @Query(SqlQueries.FIND_LAST_BY_ORDER_ID_SQL_QUERY)
    suspend fun findLastByOrderId(orderId: Long): OrderTracking?

    @Query(SqlQueries.SET_ORDER_TRACKING_STATUS_SQL_QUERY)
    suspend fun setStatus(
        orderId: Long,
        pointNumber: Int,
        status: String,
        deliveredAt: LocalDateTime?
    ): OrderTracking?

    @Query(SqlQueries.UPDATE_ORDER_TRACKING_SQL_QUERY)
    suspend fun update(
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
        estimatedDeliveryAt: LocalDateTime?,
        deliveredAt: LocalDateTime?
    ): OrderTracking?

    @Query(SqlQueries.DELETE_ALL_ORDER_TRACKINGS_BY_ORDER_ID_SQL_QUERY)
    suspend fun deleteAllByOrderId(orderId: Long): List<OrderTracking>

    @Query(SqlQueries.DELETE_ORDER_TRACKING_SQL_QUERY)
    suspend fun deleteByExternalId(orderId: Long, pointNumber: Int): OrderTracking?

}
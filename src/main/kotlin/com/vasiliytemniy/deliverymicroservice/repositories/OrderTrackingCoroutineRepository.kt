package com.vasiliytemniy.deliverymicroservice.repositories

import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import com.vasiliytemniy.deliverymicroservice.repositories.LongSqlQueries.Companion.ORDER_TRACKING_UPDATE_SQL_QUERY
import com.vasiliytemniy.deliverymicroservice.repositories.LongSqlQueries.Companion.SET_ORDER_TRACKING_STATUS_SQL_QUERY
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Repository
interface OrderTrackingCoroutineRepository : CoroutineCrudRepository<OrderTracking, Long> {

    //    @Query("SELECT * FROM order_trackings WHERE order_id = :orderId")
    fun findPageByOrderId(orderId: Long, pageable: Pageable): Flow<OrderTracking>

    //    @Query("SELECT * FROM order_trackings WHERE order_id = :orderId")
    fun findAllByOrderId(orderId: Long): Flow<OrderTracking>

    //    @Query("SELECT * FROM order_trackings WHERE carrier_id = :carrierId")
    fun findPageByCarrierId(carrierId: Long, pageable: Pageable): Flow<OrderTracking>

    //    @Query("SELECT * FROM order_trackings WHERE carrier_id = :carrierId")
    fun findAllByCarrierId(carrierId: Long): Flow<OrderTracking>

    @Query("SELECT * FROM order_trackings WHERE order_id = :orderId ORDER BY point_number DESC LIMIT 1")
    suspend fun findLastByOrderId(orderId: Long): OrderTracking

    @Query("SELECT * FROM order_trackings WHERE carrier_id = :carrierId AND delivered_at IS NULL")
    fun findPageActiveByCarrierId(carrierId: Long, pageable: Pageable): Flow<OrderTracking>

    @Query(SET_ORDER_TRACKING_STATUS_SQL_QUERY)
    suspend fun setOrderTrackingStatus(
        orderId: Long,
        pointNumber: Int,
        status: String,
        deliveredAt: String?
    ): OrderTracking

    @Query(ORDER_TRACKING_UPDATE_SQL_QUERY)
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

    @Query("DELETE FROM order_trackings WHERE order_id = :orderId")
    suspend fun deleteAllByOrderId(orderId: Long): Unit

    @Query("DELETE FROM order_trackings WHERE order_id = :orderId AND point_number = :pointNumber")
    suspend fun deleteByOrderTrackingIdentifier(orderId: Long, pointNumber: Int): Unit

}
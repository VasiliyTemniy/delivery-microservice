package com.vasiliytemniy.deliverymicroservice.repositories

import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import com.vasiliytemniy.deliverymicroservice.repositories.LongSqlQueries.Companion.ORDER_TRACKING_UPDATE_SQL_QUERY
import com.vasiliytemniy.deliverymicroservice.repositories.LongSqlQueries.Companion.SET_ORDER_TRACKING_STATUS_SQL_QUERY
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Repository
interface OrderTrackingReactiveRepository : ReactiveCrudRepository<OrderTracking, Long> {

    //    @Query("SELECT * FROM order_trackings WHERE order_id = :orderId")
    fun findPageByOrderId(orderId: Long, pageable: Pageable): Flux<OrderTracking>

    //    @Query("SELECT * FROM order_trackings WHERE order_id = :orderId")
    fun findAllByOrderId(orderId: Long): Flux<OrderTracking>

    //    @Query("SELECT * FROM order_trackings WHERE carrier_id = :carrierId")
    fun findPageByCarrierId(carrierId: Long, pageable: Pageable): Flux<OrderTracking>

    //    @Query("SELECT * FROM order_trackings WHERE carrier_id = :carrierId")
    fun findAllByCarrierId(carrierId: Long): Flux<OrderTracking>

    @Query("SELECT * FROM order_trackings WHERE order_id = :orderId ORDER BY point_number DESC LIMIT 1")
    fun findLastByOrderId(orderId: Long): Mono<OrderTracking>

    @Query("SELECT * FROM order_trackings WHERE carrier_id = :carrierId AND delivered_at IS NULL")
    fun findPageActiveByCarrierId(carrierId: Long, pageable: Pageable): Flux<OrderTracking>

    @Query(SET_ORDER_TRACKING_STATUS_SQL_QUERY)
    fun setOrderTrackingStatus(
        orderId: Long,
        pointNumber: Int,
        status: String,
        deliveredAt: String?
    ): Mono<OrderTracking>

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
    ): Mono<OrderTracking>

    @Query("DELETE FROM order_trackings WHERE order_id = :orderId")
    fun deleteAllByOrderId(orderId: Long): Mono<Void>

    @Query("DELETE FROM order_trackings WHERE order_id = :orderId AND point_number = :pointNumber")
    fun deleteByOrderTrackingIdentifier(orderId: Long, pointNumber: Int): Mono<Void>

}
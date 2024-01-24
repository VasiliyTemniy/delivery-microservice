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
import com.vasiliytemniy.deliverymicroservice.repositories.SqlQueries.Companion.SET_ORDER_TRACKING_STATUS_SQL_QUERY
import com.vasiliytemniy.deliverymicroservice.repositories.SqlQueries.Companion.UPDATE_ORDER_TRACKING_SQL_QUERY
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Repository
interface OrderTrackingReactiveRepository : ReactiveCrudRepository<OrderTracking, Long> {

    @Query(FIND_PAGE_BY_ORDER_ID_SQL_QUERY)
    fun findPageByOrderId(orderId: Long, pageable: Pageable): Flux<OrderTracking>

    @Query(FIND_ALL_BY_ORDER_ID_SQL_QUERY)
    fun findAllByOrderId(orderId: Long): Flux<OrderTracking>

    @Query(FIND_LAST_BY_ORDER_ID_SQL_QUERY)
    fun findLastByOrderId(orderId: Long): Mono<OrderTracking>

    @Query(FIND_PAGE_BY_CARRIER_ID_SQL_QUERY)
    fun findPageByCarrierId(carrierId: Long, pageable: Pageable): Flux<OrderTracking>

    @Query(FIND_ALL_BY_CARRIER_ID_SQL_QUERY)
    fun findAllByCarrierId(carrierId: Long): Flux<OrderTracking>

    @Query(FIND_PAGE_ACTIVE_BY_CARRIER_ID_SQL_QUERY)
    fun findPageActiveByCarrierId(carrierId: Long, pageable: Pageable): Flux<OrderTracking>

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
    ): Mono<OrderTracking>

    @Query(SET_ORDER_TRACKING_STATUS_SQL_QUERY)
    fun setOrderTrackingStatus(
        orderId: Long,
        pointNumber: Int,
        status: String,
        deliveredAt: String?
    ): Mono<OrderTracking>

    @Query(DELETE_ALL_ORDER_TRACKINGS_BY_ORDER_ID_SQL_QUERY)
    fun deleteAllByOrderId(orderId: Long): Mono<Void>

    @Query(DELETE_ORDER_TRACKING_SQL_QUERY)
    fun deleteByOrderTrackingIdentifier(orderId: Long, pointNumber: Int): Mono<Void>

}
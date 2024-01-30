package com.vasiliytemniy.deliverymicroservice.repositories

import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime


@Repository
interface OrderTrackingReactiveRepository : ReactiveCrudRepository<OrderTracking, String>, OrderTrackingReactiveCustomRepository {

    fun findPageByOrderId(orderId: String, pageable: Pageable): Flux<OrderTracking>

    fun findAllByOrderId(orderId: String): Flux<OrderTracking>

    @Query(SqlQueries.FIND_LAST_BY_ORDER_ID_SQL_QUERY)
    fun findLastByOrderId(orderId: String): Mono<OrderTracking>

    /**
     * Find by external id
     */
    fun findByOrderIdAndPointNumber(orderId: String, pointNumber: Int): Mono<OrderTracking>

    @Query(SqlQueries.UPDATE_ORDER_TRACKING_SQL_QUERY)
    fun update(
        orderId: String,
        pointNumber: Int,
        fromFacilityId: String,
        destinationId: String,
        destinationType: String,
        carrierId: String,
        status: String,
        deliveryCost: Int,
        currency: String,
        currencyDecimalMultiplier: Int,
        massControlValue: Int?,
        massMeasure: String?,
        lat: Double?,
        lon: Double?,
        estimatedDeliveryAt: LocalDateTime?,
        deliveredAt: LocalDateTime?
    ): Mono<OrderTracking>

    @Query(SqlQueries.SET_ORDER_TRACKING_STATUS_SQL_QUERY)
    fun setStatus(
        orderId: String,
        pointNumber: Int,
        status: String,
        deliveredAt: LocalDateTime?
    ): Mono<OrderTracking>

    @Query(SqlQueries.DELETE_ALL_ORDER_TRACKINGS_BY_ORDER_ID_SQL_QUERY)
    fun deleteAllByOrderId(orderId: String): Flux<OrderTracking>

    @Query(SqlQueries.DELETE_ORDER_TRACKING_SQL_QUERY)
    fun deleteByExternalId(orderId: String, pointNumber: Int): Mono<OrderTracking>

}
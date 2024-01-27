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
interface OrderTrackingReactiveRepository : ReactiveCrudRepository<OrderTracking, Long> {

    fun findPageByOrderId(orderId: Long, pageable: Pageable): Flux<OrderTracking>

    fun findAllByOrderId(orderId: Long): Flux<OrderTracking>

    @Query(SqlQueries.FIND_LAST_BY_ORDER_ID_SQL_QUERY)
    fun findLastByOrderId(orderId: Long): Mono<OrderTracking>

    fun findPageByCarrierId(carrierId: Long, pageable: Pageable): Flux<OrderTracking>

    fun findAllByCarrierId(carrierId: Long): Flux<OrderTracking>

    // A tricky workaround to filter active order trackings with deliveredAt without custom impl
    fun findPageByCarrierIdAndDeliveredAt(carrierId: Long, deliveredAt: LocalDateTime?, pageable: Pageable): Flux<OrderTracking>
    fun findAllByCarrierIdAndDeliveredAt(carrierId: Long, deliveredAt: LocalDateTime?): Flux<OrderTracking>

    @Query(SqlQueries.UPDATE_ORDER_TRACKING_SQL_QUERY)
    fun update(
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
    ): Mono<OrderTracking>

    @Query(SqlQueries.SET_ORDER_TRACKING_STATUS_SQL_QUERY)
    fun setStatus(
        orderId: Long,
        pointNumber: Int,
        status: String,
        deliveredAt: LocalDateTime?
    ): Mono<OrderTracking>

    @Query(SqlQueries.DELETE_ALL_ORDER_TRACKINGS_BY_ORDER_ID_SQL_QUERY)
    fun deleteAllByOrderId(orderId: Long): Flux<OrderTracking>

    @Query(SqlQueries.DELETE_ORDER_TRACKING_SQL_QUERY)
    fun deleteByExternalId(orderId: Long, pointNumber: Int): Mono<OrderTracking>

}
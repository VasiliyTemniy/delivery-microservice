package com.vasiliytemniy.deliverymicroservice.repositories

import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import com.vasiliytemniy.deliverymicroservice.domain.of
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.r2dbc.core.DatabaseClient
import reactor.core.publisher.Mono

class OrderTrackingReactiveCustomRepositoryImpl(
    val template: R2dbcEntityTemplate,
    val databaseClient: DatabaseClient
): OrderTrackingReactiveCustomRepository {

    override fun setPointNumber(orderId: String, fromPointNumber: Int, toPointNumber: Int): Mono<OrderTracking> =
        databaseClient.sql(SqlQueries.SET_POINT_NUMBER_SQL_QUERY)
            .bind("orderId", orderId)
            .bind("fromPointNumber", fromPointNumber)
            .bind("toPointNumber", toPointNumber)
            .fetch()
            .first()
            .flatMap { Mono.just(OrderTracking.of(it)) }

}
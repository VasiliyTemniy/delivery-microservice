package com.vasiliytemniy.deliverymicroservice.repositories

import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import com.vasiliytemniy.deliverymicroservice.domain.of
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.isEqual
import org.springframework.r2dbc.core.DatabaseClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class OrderTrackingReactiveCustomRepositoryImpl(
    val template: R2dbcEntityTemplate,
    val databaseClient: DatabaseClient
): OrderTrackingReactiveCustomRepository {

    override fun findPageByCarrierId(
        carrierId: String,
        pageable: Pageable,
        filterActive: Boolean
    ): Flux<OrderTracking> {
        val query = if (filterActive)
            Query.query(Criteria.where("carrier_id").isEqual(carrierId))
        else
            Query.query(Criteria.where("carrier_id").isEqual(carrierId).and("delivered_at").isNull())

        return template.select(query.with(pageable), OrderTracking::class.java)
    }

    override fun findAllByCarrierId(carrierId: String, filterActive: Boolean): Flux<OrderTracking> {
        val query = if (filterActive)
            Query.query(Criteria.where("carrier_id").isEqual(carrierId))
        else
            Query.query(Criteria.where("carrier_id").isEqual(carrierId).and("delivered_at").isNull())

        return template.select(query, OrderTracking::class.java)
    }

    override fun setPointNumber(orderId: String, fromPointNumber: Int, toPointNumber: Int): Mono<OrderTracking> =
        databaseClient.sql(SqlQueries.SET_POINT_NUMBER_SQL_QUERY)
            .bind("orderId", orderId)
            .bind("fromPointNumber", fromPointNumber)
            .bind("toPointNumber", toPointNumber)
            .fetch()
            .first()
            .flatMap { Mono.just(OrderTracking.of(it)) }

    override fun countByOrderId(orderId: String): Mono<Long> =
        databaseClient.sql(SqlQueries.SELECT_COUNT_BY_ORDER_ID_SQL_QUERY)
            .bind("orderId", orderId)
            .fetch()
            .first()
            .flatMap { Mono.just(it["total"] as Long) }

    override fun countByCarrierId(carrierId: String, filterActive: Boolean): Mono<Long> {
        val countQuery = if (filterActive)
            SqlQueries.SELECT_COUNT_ACTIVE_BY_CARRIER_ID_SQL_QUERY
        else
            SqlQueries.SELECT_COUNT_BY_CARRIER_ID_SQL_QUERY

        return databaseClient.sql(countQuery)
            .bind("carrierId", carrierId)
            .fetch()
            .first()
            .flatMap { Mono.just(it["total"] as Long) }
    }

    companion object {
        private val log = LoggerFactory.getLogger(OrderTrackingReactiveCustomRepositoryImpl::class.java)
    }

}
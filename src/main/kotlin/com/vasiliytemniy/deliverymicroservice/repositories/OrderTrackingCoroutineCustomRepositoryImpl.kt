package com.vasiliytemniy.deliverymicroservice.repositories

import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking.Companion.ORDER_ID
import com.vasiliytemniy.deliverymicroservice.domain.of
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.isEqual
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository


@Repository
class OrderTrackingCoroutineCustomRepositoryImpl(
    private val template: R2dbcEntityTemplate,
    private val databaseClient: DatabaseClient
) : OrderTrackingCoroutineCustomRepository {

    override suspend fun findPageByOrderId(orderId: String, pageable: Pageable): Page<OrderTracking> =
        withContext(Dispatchers.IO) {
            val query = Query.query(Criteria.where(ORDER_ID).isEqual(orderId))

            val orderTrackingList = async {
                template.select(query.with(pageable), OrderTracking::class.java)
                    .asFlow()
                    .toList()
            }

            val totalCount = async {
                databaseClient.sql(SqlQueries.SELECT_COUNT_BY_ORDER_ID_SQL_QUERY)
                    .bind("orderId", orderId)
                    .fetch()
                    .one()
                    .awaitFirst()
            }

            PageImpl(orderTrackingList.await(), pageable, totalCount.await()["total"] as Long)
                .also { log.debug("pagination: {}", it) }
        }

    override suspend fun findPageByCarrierId(
        carrierId: String,
        pageable: Pageable,
        filterActive: Boolean
    ): Page<OrderTracking> =
        withContext(Dispatchers.IO) {

            val totalCount = async {
                databaseClient.sql(SqlQueries.SELECT_COUNT_BY_CARRIER_ID_SQL_QUERY)
                    .bind("carrierId", carrierId)
                    .fetch()
                    .one()
                    .awaitFirst()
            }

            val query = if (filterActive)
                Query.query(Criteria.where("carrier_id").isEqual(carrierId))
            else
                Query.query(Criteria.where("carrier_id").isEqual(carrierId).and("delivered_at").isNull())

            val orderTrackingList = async {
                template.select(query.with(pageable), OrderTracking::class.java)
                    .asFlow()
                    .toList()
            }

            PageImpl(orderTrackingList.await(), pageable, totalCount.await()["total"] as Long)
                .also { log.debug("pagination: {}", it) }
        }

    override fun findAllByCarrierId(carrierId: String, filterActive: Boolean): Flow<OrderTracking> =
        if (filterActive) {
            template.select(
                Query.query(Criteria.where("carrier_id").isEqual(carrierId)),
                OrderTracking::class.java
            ).asFlow()
        } else {
            template.select(
                Query.query(Criteria.where("carrier_id").isEqual(carrierId).and("delivered_at").isNull()),
                OrderTracking::class.java
            ).asFlow()
        }

    override suspend fun setPointNumber(orderId: String, fromPointNumber: Int, toPointNumber: Int): OrderTracking? =
        withContext(Dispatchers.IO) {
            databaseClient.sql(SqlQueries.SET_POINT_NUMBER_SQL_QUERY)
                .bind("orderId", orderId)
                .bind("fromPointNumber", fromPointNumber)
                .bind("toPointNumber", toPointNumber)
                .fetch()
                .first()
                .map { OrderTracking.of(it) }
                .awaitFirst()
        }

    companion object {
        private val log = LoggerFactory.getLogger(OrderTrackingCoroutineCustomRepositoryImpl::class.java)
    }
}
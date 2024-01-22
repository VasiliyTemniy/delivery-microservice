//package com.vasiliytemniy.deliverymicroservice.repositories
//
//import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
//import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking.Companion.ORDER_ID
//import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking.Companion.CARRIER_ID
//import com.vasiliytemniy.deliverymicroservice.utils.runWithTracing
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.async
//import kotlinx.coroutines.flow.toList
//import kotlinx.coroutines.reactive.asFlow
//import kotlinx.coroutines.reactive.awaitFirst
//import kotlinx.coroutines.withContext
//import org.slf4j.LoggerFactory
//import io.micrometer.tracing.Tracer
//import io.micrometer.tracing.instrument.kotlin.asContextElement
//import org.springframework.data.domain.Page
//import org.springframework.data.domain.PageImpl
//import org.springframework.data.domain.Pageable
//import org.springframework.data.relational.core.query.Criteria
//import org.springframework.data.relational.core.query.Query
//import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
//import org.springframework.data.relational.core.query.isEqual
//import org.springframework.r2dbc.core.DatabaseClient
//import org.springframework.stereotype.Repository
//
//
//@Repository
//class OrderTrackingPostgresRespositoryImpl(
//    private val template: R2dbcEntityTemplate,
//    private val databaseClient: DatabaseClient,
//    private val tracer: Tracer
//) : OrderTrackingPostgresRepository {
//
//    override suspend fun findByOrderId(orderId: Long, pageable: Pageable): Page<OrderTracking> =
//        withContext(Dispatchers.IO + tracer.asContextElement()) {
//            val span = tracer.startScopedSpan(FIND_BY_ORDER_ID)
//            val query = Query.query(Criteria.where(ORDER_ID).isEqual(orderId))
//
//            runWithTracing(span) {
//                val orderTrackingsList = async {
//                    template.select(query.with(pageable), OrderTracking::class.java)
//                        .asFlow()
//                        .toList()
//                }
//
//                val totalCount = async {
//                    databaseClient.sql("SELECT count(id) as total FROM delivery.order_trackings WHERE order_id = :orderId")
//                        .bind("orderId", orderId)
//                        .fetch()
//                        .one()
//                        .awaitFirst()
//                }
//
//                PageImpl(orderTrackingsList.await(), pageable, totalCount.await()["total"] as Long)
//                    .also { span.tag("pagination", it.toString()) }
//                    .also { log.debug("pagination: $it") }
//            }
//        }
//
//    override suspend fun findByCarrierId(carrierId: Long, pageable: Pageable): Page<OrderTracking> =
//        withContext(Dispatchers.IO + tracer.asContextElement()) {
//            val span = tracer.startScopedSpan(FIND_BY_ORDER_ID)
//            val query = Query.query(Criteria.where(CARRIER_ID).isEqual(carrierId))
//
//            runWithTracing(span) {
//                val orderTrackingsList = async {
//                    template.select(query.with(pageable), OrderTracking::class.java)
//                        .asFlow()
//                        .toList()
//                }
//
//                val totalCount = async {
//                    databaseClient.sql("SELECT count(id) as total FROM delivery.order_trackings WHERE carrier_id = :carrierId")
//                        .bind("carrierId", carrierId)
//                        .fetch()
//                        .one()
//                        .awaitFirst()
//                }
//
//                PageImpl(orderTrackingsList.await(), pageable, totalCount.await()["total"] as Long)
//                    .also { span.tag("pagination", it.toString()) }
//                    .also { log.debug("pagination: $it") }
//            }
//        }
//
//    companion object {
//        private val log = LoggerFactory.getLogger(OrderTrackingPostgresRespositoryImpl::class.java)
//        private const val FIND_BY_ORDER_ID = "OrderTrackingPostgresRespository.findByOrderId"
//        private const val FIND_BY_CARRIER_ID = "OrderTrackingPostgresRespository.findByCarrierId"
//    }
//}
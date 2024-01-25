package com.vasiliytemniy.deliverymicroservice.controllers.grpc

import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import com.vasiliytemniy.deliverymicroservice.domain.of
import com.vasiliytemniy.deliverymicroservice.domain.toProto
import com.vasiliytemniy.deliverymicroservice.dto.*
import com.vasiliytemniy.deliverymicroservice.interceptors.LogGrpcInterceptor
import com.vasiliytemniy.deliverymicroservice.services.OrderTrackingCoroutineService
import net.devh.boot.grpc.server.service.GrpcService
import jakarta.validation.Validator
import jakarta.validation.ConstraintViolationException
import com.vasiliytemniy.grpc.ordertracking.service.OrderTracking.*
import com.vasiliytemniy.grpc.ordertracking.service.OrderTrackingServiceGrpcKt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeout
import org.slf4j.LoggerFactory


@GrpcService(interceptors = [LogGrpcInterceptor::class])
class OrderTrackingGrpcService(
    private val orderTrackingService: OrderTrackingCoroutineService, private val validator: Validator
) : OrderTrackingServiceGrpcKt.OrderTrackingServiceCoroutineImplBase() {

    override suspend fun createOrderTracking(request: CreateOrderTrackingRequest): OrderTrackingResponse =
        withTimeout(TIMEOUT_MILLIS) {
            orderTrackingService.createOrderTracking(validate(OrderTracking.of(request)))
                .let { OrderTrackingResponse.newBuilder().setOrderTracking(it.toProto()).build() }
                .also { log.info("created order tracking: $request") }
        }

    override fun getOrderTrackingsByOrderId(request: GetOrderTrackingsByOrderIdRequest): Flow<ManyOrderTrackingsWithPaginationResponse> {
        var index = 0
        return orderTrackingService.getOrderTrackingsByOrderIdFlow(validate(GetOrderTrackingsByOrderIdDto.of(request)))
            .map {
                ManyOrderTrackingsWithPaginationResponse.newBuilder().setOrderTracking(index++, it.toProto()).build()
            }.flowOn(Dispatchers.IO)
    }

    override suspend fun getLastOrderTrackingByOrderId(request: GetLastOrderTrackingByOrderIdRequest): OrderTrackingResponse =
        withTimeout(TIMEOUT_MILLIS) {
            orderTrackingService.getLastOrderTrackingByOrderId(validate(request.orderId))?.toProto()
                .let { OrderTrackingResponse.newBuilder().setOrderTracking(it).build() }
                .also { log.info("got last order tracking: $request") }
        }

    override fun getOrderTrackingsByCarrierId(request: GetOrderTrackingsByCarrierIdRequest): Flow<ManyOrderTrackingsWithPaginationResponse> {
        var index = 0
        return orderTrackingService.getOrderTrackingsByCarrierIdFlow(
            validate(GetOrderTrackingsByCarrierIdDto.of(request)), request.filterActive
        ).map {
                ManyOrderTrackingsWithPaginationResponse.newBuilder().setOrderTracking(index++, it.toProto()).build()
            }.flowOn(Dispatchers.IO)
    }

    override suspend fun updateOrderTracking(request: UpdateOrderTrackingRequest): OrderTrackingResponse =
        withTimeout(TIMEOUT_MILLIS) {
            orderTrackingService.updateOrderTracking(validate(UpdateOrderTrackingDto.of(request)))?.toProto()
                .let { OrderTrackingResponse.newBuilder().setOrderTracking(it).build() }
                .also { log.info("updated order tracking: $request") }
        }

    override suspend fun setOrderTrackingStatuses(request: SetOrderTrackingStatusesRequest): ManyOrderTrackingsResponse =
        withTimeout(TIMEOUT_MILLIS) {
            val builder = ManyOrderTrackingsResponse.newBuilder()
            val orderTrackings =
                orderTrackingService.setOrderTrackingStatuses(validate(SetOrderTrackingStatusesDto.of(request)))

            for (it in orderTrackings) {
                builder.addOrderTracking(it.toProto())
            }

            builder.build()
        }

    override suspend fun reorderOrderTrackings(request: ReorderOrderTrackingsRequest): ManyOrderTrackingsResponse {
        throw NotImplementedError()
    }

    override suspend fun deleteOrderTrackingsByOrderId(request: DeleteOrderTrackingsByOrderIdRequest): ManyOrderTrackingsResponse =
        withTimeout(TIMEOUT_MILLIS) {
            val builder = ManyOrderTrackingsResponse.newBuilder()
            val orderTrackings = orderTrackingService.deleteAllByOrderId(validate(request.orderId))

            for (it in orderTrackings) {
                builder.addOrderTracking(it.toProto())
            }

            builder.build()
        }

    override suspend fun deleteByOrderTrackingIdentifier(request: DeleteByOrderTrackingIdentifierRequest): OrderTrackingResponse =
        withTimeout(TIMEOUT_MILLIS) {
            orderTrackingService.deleteByOrderTrackingIdentifier(
                validate(request.orderId), validate(request.pointNumber)
            ).toProto()
                .let { OrderTrackingResponse.newBuilder().setOrderTracking(it).build() }
                .also { log.info("deleted order tracking: $request") }
        }

    private fun <T> validate(data: T): T {
        return data.run {
            val errors = validator.validate(data)
            if (errors.isNotEmpty()) throw ConstraintViolationException(errors).also { log.error("validation error: ${it.localizedMessage}") }
            data
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(OrderTrackingGrpcService::class.java)
        const val TIMEOUT_MILLIS = 5000L
    }
}
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
import org.springframework.data.domain.Page


@GrpcService(interceptors = [LogGrpcInterceptor::class])
class OrderTrackingGrpcService(
    private val orderTrackingService: OrderTrackingCoroutineService,
    private val validator: Validator
) : OrderTrackingServiceGrpcKt.OrderTrackingServiceCoroutineImplBase() {

    override suspend fun create(request: CreateRequest): OrderTrackingResponse =
        withTimeout(TIMEOUT_MILLIS) {
            orderTrackingService.create(validate(OrderTracking.of(request)))
                .let { OrderTrackingResponse.newBuilder().setOrderTracking(it.toProto()).build() }
                .also { log.info("create response: $it") }
        }

    override fun getStreamByOrderId(request: GetStreamByOrderIdRequest): Flow<OrderTrackingResponse> =
        orderTrackingService.getFlowByOrderId(validate(request.orderId))
            .map { OrderTrackingResponse.newBuilder().setOrderTracking(it.toProto()).build() }
            .flowOn(Dispatchers.IO)

    override suspend fun getPageByOrderId(request: GetPageByOrderIdRequest): PageOrderTrackingsResponse =
        orderTrackingService.getPageByOrderId(validate(GetOrderTrackingsByOrderIdDto.of(request)))
            .toPageOrderTrackingsResponse()
            .also { log.info("getPageByOrderId response: $it") }

    override suspend fun getLastByOrderId(request: GetLastByOrderIdRequest): OrderTrackingResponse =
        withTimeout(TIMEOUT_MILLIS) {
            orderTrackingService.getLastByOrderId(validate(request.orderId))?.toProto()
                .let { OrderTrackingResponse.newBuilder().setOrderTracking(it).build() }
                .also { log.info("getLastByOrderId response: $it") }
        }

    override fun getStreamByCarrierId(request: GetStreamByCarrierIdRequest): Flow<OrderTrackingResponse> =
        orderTrackingService.getFlowByCarrierId(validate(request.carrierId), validate(request.filterActive))
            .map { OrderTrackingResponse.newBuilder().setOrderTracking(it.toProto()).build() }
            .flowOn(Dispatchers.IO)

    override suspend fun getPageByCarrierId(request: GetPageByCarrierIdRequest): PageOrderTrackingsResponse =
        orderTrackingService.getPageByCarrierId(validate(GetOrderTrackingsByCarrierIdDto.of(request)))
            .toPageOrderTrackingsResponse()
            .also { log.info("getPageByCarrierId response: $it") }

    override suspend fun setStatuses(request: SetStatusesRequest): ManyOrderTrackingsResponse =
        withTimeout(TIMEOUT_MILLIS) {
            ManyOrderTrackingsResponse.newBuilder().addAllOrderTracking(
                orderTrackingService.setStatuses(validate(SetOrderTrackingStatusesDto.of(request)))
                    .map { it.toProto() }
            ).build()
                .also { log.info("setStatuses response: $it") }
        }

    override suspend fun reorder(request: ReorderRequest): ManyOrderTrackingsResponse =
        withTimeout(TIMEOUT_MILLIS) {
            ManyOrderTrackingsResponse.newBuilder().addAllOrderTracking(
                orderTrackingService.reorder(validate(ReorderOrderTrackingsDto.of(request)))
                    .map { it.toProto() }
            ).build()
                .also { log.info("reorder response: $it") }
        }

    override suspend fun update(request: UpdateRequest): OrderTrackingResponse =
        withTimeout(TIMEOUT_MILLIS) {
            orderTrackingService.update(validate(UpdateOrderTrackingDto.of(request)))?.toProto()
                .let { OrderTrackingResponse.newBuilder().setOrderTracking(it).build() }
                .also { log.info("update response: $it") }
        }

    override suspend fun deleteByOrderId(request: DeleteByOrderIdRequest): ManyOrderTrackingsResponse =
        withTimeout(TIMEOUT_MILLIS) {
            ManyOrderTrackingsResponse.newBuilder().addAllOrderTracking(
                orderTrackingService.deleteAllByOrderId(validate(request.orderId))
                    .map { it.toProto() }
            ).build()
                .also { log.info("deleteByOrderId response: $it") }
        }

    override suspend fun deleteByExternalId(request: DeleteByExternalIdRequest): OrderTrackingResponse =
        withTimeout(TIMEOUT_MILLIS) {
            orderTrackingService.deleteByExternalId(
                validate(request.orderId), validate(request.pointNumber)
            )?.toProto()
                .let { OrderTrackingResponse.newBuilder().setOrderTracking(it).build() }
                .also { log.info("deleteByExternalId response: $it") }
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

fun Page<OrderTracking>.toPageOrderTrackingsResponse(): PageOrderTrackingsResponse {
    return PageOrderTrackingsResponse
        .newBuilder()
        .setIsFirst(this.isFirst)
        .setIsLast(this.isLast)
        .setTotalElements(this.totalElements.toInt())
        .setTotalPages(this.totalPages)
        .setPage(this.pageable.pageNumber)
        .setSize(this.pageable.pageSize)
        .addAllOrderTracking(this.content.map { it.toProto() })
        .build()
}
package com.vasiliytemniy.deliverymicroservice.controllers.http

import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import com.vasiliytemniy.deliverymicroservice.domain.of
import com.vasiliytemniy.deliverymicroservice.domain.toSuccessHttpResponse
import com.vasiliytemniy.deliverymicroservice.dto.*
import com.vasiliytemniy.deliverymicroservice.services.OrderTrackingCoroutineService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeout
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Order Tracking", description = "Order Tracking REST API")
@RestController
@RequestMapping(path = ["/api/v1/coroutine/order-tracking"])
class OrderTrackingCoroutineController(
    private val orderTrackingCoroutineService: OrderTrackingCoroutineService
) {

    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "createOrderTracking",
        summary = "Create order tracking",
        operationId = "createOrderTracking",
        description = "Create new order tracking"
    )
    suspend fun createOrderTracking(
        @Valid @RequestBody req: CreateOrderTrackingDto
    ): ResponseEntity<SuccessOrderTrackingResponse> = withTimeout(TIMEOUT_MILLIS) {
        ResponseEntity.status(HttpStatus.CREATED)
            .body(orderTrackingCoroutineService.createOrderTracking(OrderTracking.of(req)).toSuccessHttpResponse())
            .also { log.info("created order tracking: $req") }
    }

    @GetMapping(path = ["/flow/by-order-id/{orderId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "getOrderTrackingsByOrderIdFlow",
        summary = "Get order trackings by order id",
        operationId = "getOrderTrackingsByOrderIdFlow",
        description = "Get order trackings by order id with pagination"
    )
    fun getOrderTrackingsByOrderIdFlow(
        @PathVariable(required = true) orderId: Long,
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "10") size: Int
    ): Flow<SuccessOrderTrackingResponse> = orderTrackingCoroutineService.getOrderTrackingsByOrderIdFlow(
        GetOrderTrackingsByOrderIdDto(
            orderId, PageRequest.of(page, size)
        )
    ).map { it -> it.toSuccessHttpResponse().also { log.info("response: $it") } }

    @GetMapping(path = ["/flow/by-carrier-id/{carrierId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "getOrderTrackingsByCarrierIdFlow",
        summary = "Get order trackings by carrier id",
        operationId = "getOrderTrackingsByCarrierIdFlow",
        description = "Get order trackings by carrier id with pagination"
    )
    fun getOrderTrackingsByCarrierIdFlow(
        @PathVariable(required = true) carrierId: Long,
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "10") size: Int,
        @RequestParam(name = "filter-active", defaultValue = "true") filterActive: Boolean
    ): Flow<SuccessOrderTrackingResponse> = orderTrackingCoroutineService.getOrderTrackingsByCarrierIdFlow(
        GetOrderTrackingsByCarrierIdDto(
            carrierId, PageRequest.of(page, size)
        ), filterActive
    ).map { it -> it.toSuccessHttpResponse().also { log.info("response: $it") } }

    @GetMapping(path = ["/last/{orderId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "getLastOrderTrackingByOrderId",
        summary = "Get last order tracking by order id",
        operationId = "getLastOrderTrackingByOrderId",
        description = "Get last order tracking by order id"
    )
    suspend fun getLastOrderTrackingByOrderId(
        @PathVariable(required = true) orderId: Long
    ): ResponseEntity<SuccessOrderTrackingResponse?> = withTimeout(TIMEOUT_MILLIS) {
        ResponseEntity.status(HttpStatus.OK)
            .body(orderTrackingCoroutineService.getLastOrderTrackingByOrderId(orderId)?.toSuccessHttpResponse())
            .also { log.info("get last order tracking by order id: $orderId") }
    }

    @PutMapping(path = ["/status"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "setOrderTrackingStatus",
        summary = "Set order tracking status",
        operationId = "setOrderTrackingStatus",
        description = "Set order tracking status"
    )
    suspend fun setOrderTrackingStatus(
        @Valid @RequestBody req: SetOrderTrackingStatusesDto
    ) = withTimeout(TIMEOUT_MILLIS) {
        ResponseEntity.status(HttpStatus.OK).body(
            orderTrackingCoroutineService.setOrderTrackingStatuses(
                SetOrderTrackingStatusesDto(
                    req.orderTrackingIdentifiers, req.status, req.deliveredAt
                )
            )
        ).also { log.info("set order tracking status: $req") }
    }

    @DeleteMapping(path = ["/{orderId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "deleteAllByOrderId",
        summary = "Delete all order trackings by order id",
        operationId = "deleteAllByOrderId",
        description = "Delete all order trackings by order id"
    )
    suspend fun deleteAllByOrderId(
        @PathVariable(required = true) orderId: Long
    ): ResponseEntity<List<OrderTracking>> = withTimeout(TIMEOUT_MILLIS) {
        ResponseEntity
            .status(HttpStatus.GONE)
            .body(orderTrackingCoroutineService.deleteAllByOrderId(orderId))
            .also { log.info("deleted order tracking: $orderId") }
    }

    @DeleteMapping(path = ["/{orderId}/{pointNumber}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "deleteByOrderTrackingIdentifier",
        summary = "Delete order tracking by order id and point number",
        operationId = "deleteByOrderTrackingIdentifier",
        description = "Delete order tracking by order id and point number"
    )
    suspend fun deleteByOrderTrackingIdentifier(
        @PathVariable(required = true) orderId: Long,
        @PathVariable(required = true) pointNumber: Int
    ): ResponseEntity<OrderTracking> = withTimeout(TIMEOUT_MILLIS) {
        ResponseEntity
            .status(HttpStatus.GONE)
            .body(orderTrackingCoroutineService.deleteByOrderTrackingIdentifier(orderId, pointNumber))
            .also { log.info("deleted order tracking: $orderId, $pointNumber") }
    }

    companion object {
        private val log = LoggerFactory.getLogger(OrderTrackingReactiveController::class.java)
        private const val TIMEOUT_MILLIS = 5000L
    }
}
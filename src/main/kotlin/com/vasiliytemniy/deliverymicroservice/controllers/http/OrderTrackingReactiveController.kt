package com.vasiliytemniy.deliverymicroservice.controllers.http

import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import com.vasiliytemniy.deliverymicroservice.domain.of
import com.vasiliytemniy.deliverymicroservice.dto.*
import com.vasiliytemniy.deliverymicroservice.services.OrderTrackingReactiveService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.validation.Valid

@Tag(name = "Order Tracking", description = "Order Tracking REST API")
@RestController
@RequestMapping(path = ["/api/v1/reactive/order-tracking"])
class OrderTrackingReactiveController(
    private val orderTrackingReactiveService: OrderTrackingReactiveService,
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
    ): Mono<OrderTracking> =
        orderTrackingReactiveService.createOrderTracking(OrderTracking.of(req))
            .also { log.info("created order tracking: $req") }

    @GetMapping(path = ["/all/by-order-id/{orderId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "getOrderTrackingsByOrderId",
        summary = "Get order trackings by order id",
        operationId = "getOrderTrackingsByOrderId",
        description = "Get order trackings by order id with pagination"
    )
    suspend fun getOrderTrackingsByOrderId(
        @PathVariable(required = true) orderId: Long,
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "10") size: Int
    ): Mono<Page<OrderTracking>> =
        orderTrackingReactiveService.getOrderTrackingsByOrderId(
            GetOrderTrackingsByOrderIdDto(
                orderId,
                PageRequest.of(page, size)
            )
        ).also { log.info("get order trackings by order id: $orderId, page $page, size $size") }

    @GetMapping(path = ["/all/by-carrier-id/{carrierId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "getOrderTrackingsByCarrierId",
        summary = "Get order trackings by carrier id",
        operationId = "getOrderTrackingsByCarrierId",
        description = "Get order trackings by carrier id with pagination"
    )
    suspend fun getOrderTrackingsByCarrierId(
        @PathVariable(required = true) carrierId: Long,
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "10") size: Int,
        @RequestParam(name = "filter-active", defaultValue = "true") filterActive: Boolean
    ): Mono<Page<OrderTracking>> =
        orderTrackingReactiveService.getOrderTrackingsByCarrierId(
            GetOrderTrackingsByCarrierIdDto(
                carrierId,
                PageRequest.of(page, size)
            ), filterActive
        ).also { log.info("get order trackings by carrier id: $carrierId, page $page, size $size") }

    @GetMapping(path = ["/last/{orderId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "getLastOrderTrackingByOrderId",
        summary = "Get last order tracking by order id",
        operationId = "getLastOrderTrackingByOrderId",
        description = "Get last order tracking by order id"
    )
    suspend fun getLastOrderTrackingByOrderId(
        @PathVariable(required = true) orderId: Long
    ): Mono<OrderTracking>? =
        orderTrackingReactiveService.getLastOrderTrackingByOrderId(orderId)
            .also { log.info("get last order tracking by order id: $orderId") }

    @PutMapping(path = ["/status"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "setOrderTrackingStatus",
        summary = "Set order tracking status",
        operationId = "setOrderTrackingStatus",
        description = "Set order tracking status"
    )
    suspend fun setOrderTrackingStatus(
        @Valid @RequestBody req: SetOrderTrackingStatusesDto
    ): Flux<OrderTracking> =
        orderTrackingReactiveService.setOrderTrackingStatuses(req)
            .also { log.info("set order tracking statuses: $req") }

    companion object {
        private val log = LoggerFactory.getLogger(OrderTrackingReactiveController::class.java)
    }
}
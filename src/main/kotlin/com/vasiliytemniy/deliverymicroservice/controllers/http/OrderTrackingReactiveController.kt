package com.vasiliytemniy.deliverymicroservice.controllers.http

import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import com.vasiliytemniy.deliverymicroservice.domain.of
import com.vasiliytemniy.deliverymicroservice.domain.toSuccessHttpResponse
import com.vasiliytemniy.deliverymicroservice.dto.*
import com.vasiliytemniy.deliverymicroservice.services.OrderTrackingReactiveService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import jakarta.validation.Valid

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
    fun createOrderTracking(
        @Valid @RequestBody req: CreateOrderTrackingDto
    ): ResponseEntity<Mono<SuccessOrderTrackingResponse>> =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(orderTrackingReactiveService.createOrderTracking(OrderTracking.of(req))
                .flatMap { Mono.just(it.toSuccessHttpResponse()) })
            .also { log.info("created order tracking: $req") }

    @GetMapping(path = ["/by-order-id/{orderId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "getOrderTrackingsByOrderId",
        summary = "Get order trackings by order id",
        operationId = "getOrderTrackingsByOrderId",
        description = "Get order trackings by order id with pagination"
    )
    fun getOrderTrackingsByOrderId(
        @PathVariable(required = true) orderId: Long,
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "10") size: Int
    ): ResponseEntity<Mono<Page<SuccessOrderTrackingResponse>>> =
        ResponseEntity
            .status(HttpStatus.OK)
            .body(orderTrackingReactiveService.getOrderTrackingsByOrderId(
                GetOrderTrackingsByOrderIdDto(orderId, PageRequest.of(page, size))
            ).flatMap { content ->
                Mono.just(content.map { it.toSuccessHttpResponse() })
            }).also { log.info("get order trackings by order id: $orderId, page $page, size $size") }

    @GetMapping(path = ["/by-carrier-id/{carrierId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "getOrderTrackingsByCarrierId",
        summary = "Get order trackings by carrier id",
        operationId = "getOrderTrackingsByCarrierId",
        description = "Get order trackings by carrier id with pagination"
    )
    fun getOrderTrackingsByCarrierId(
        @PathVariable(required = true) carrierId: Long,
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "10") size: Int,
        @RequestParam(name = "filter-active", defaultValue = "true") filterActive: Boolean
    ): ResponseEntity<Mono<Page<SuccessOrderTrackingResponse>>> =
        ResponseEntity
            .status(HttpStatus.OK)
            .body(orderTrackingReactiveService.getOrderTrackingsByCarrierId(
                GetOrderTrackingsByCarrierIdDto(
                    carrierId, PageRequest.of(page, size)
                ), filterActive
            ).flatMap { content ->
                Mono.just(content.map { it.toSuccessHttpResponse() })
            }).also { log.info("get order trackings by carrier id: $carrierId, page $page, size $size") }

    @GetMapping(path = ["/last/{orderId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "getLastOrderTrackingByOrderId",
        summary = "Get last order tracking by order id",
        operationId = "getLastOrderTrackingByOrderId",
        description = "Get last order tracking by order id"
    )
    fun getLastOrderTrackingByOrderId(
        @PathVariable(required = true) orderId: Long
    ): ResponseEntity<Mono<SuccessOrderTrackingResponse>> =
        ResponseEntity.status(HttpStatus.OK)
            .body(orderTrackingReactiveService.getLastOrderTrackingByOrderId(orderId)
                ?.flatMap { Mono.just(it.toSuccessHttpResponse()) })
            .also { log.info("get last order tracking by order id: $orderId") }

    @PutMapping(path = ["/status"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "setOrderTrackingStatus",
        summary = "Set order tracking status",
        operationId = "setOrderTrackingStatus",
        description = "Set order tracking status"
    )
    fun setOrderTrackingStatus(
        @Valid @RequestBody req: SetOrderTrackingStatusesDto
    ): ResponseEntity<Flux<SuccessOrderTrackingResponse>> =
        ResponseEntity.status(HttpStatus.OK)
            .body(orderTrackingReactiveService.setOrderTrackingStatuses(req)
                .flatMap { Flux.just(it.toSuccessHttpResponse()) })
            .also { log.info("set order tracking statuses: $req") }

    @DeleteMapping(path = ["/{orderId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "deleteAllByOrderId",
        summary = "Delete all order trackings by order id",
        operationId = "deleteAllByOrderId",
        description = "Delete all order trackings by order id"
    )
    fun deleteAllByOrderId(
        @PathVariable(required = true) orderId: Long
    ): ResponseEntity<Flux<SuccessOrderTrackingResponse>> =
        ResponseEntity.status(HttpStatus.GONE)
            .body(orderTrackingReactiveService.deleteAllByOrderId(orderId)
                .flatMap { Flux.just(it.toSuccessHttpResponse()) })
            .also { log.info("deleted order tracking: $orderId") }

    @DeleteMapping(path = ["/{orderId}/{pointNumber}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "deleteByOrderTrackingIdentifier",
        summary = "Delete order tracking by order id and point number",
        operationId = "deleteByOrderTrackingIdentifier",
        description = "Delete order tracking by order id and point number"
    )
    fun deleteByOrderTrackingIdentifier(
        @PathVariable(required = true) orderId: Long, @PathVariable(required = true) pointNumber: Int
    ): ResponseEntity<Mono<SuccessOrderTrackingResponse>> =
        ResponseEntity.status(HttpStatus.GONE)
            .body(orderTrackingReactiveService.deleteByOrderTrackingIdentifier(orderId, pointNumber)
                .flatMap { Mono.just(it.toSuccessHttpResponse()) })
            .also { log.info("deleted order tracking: $orderId, $pointNumber") }

    companion object {
        private val log = LoggerFactory.getLogger(OrderTrackingReactiveController::class.java)
    }
}
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
            .body(orderTrackingReactiveService.create(OrderTracking.of(req))
                .flatMap { Mono.just(it.toSuccessHttpResponse()) })
            .also { log.info("created order tracking: $req") }

    @GetMapping(path = ["/by-order-id/{orderId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "getPageOrderTrackingsByOrderId",
        summary = "Get order trackings by order id",
        operationId = "getPageOrderTrackingsByOrderId",
        description = "Get order trackings by order id with pagination"
    )
    fun getPageOrderTrackingsByOrderId(
        @PathVariable(required = true) orderId: Long,
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "10") size: Int
    ): ResponseEntity<Mono<Page<SuccessOrderTrackingResponse>>> =
        ResponseEntity
            .status(HttpStatus.OK)
            .body(orderTrackingReactiveService.getPageByOrderId(
                GetOrderTrackingsByOrderIdDto(orderId, PageRequest.of(page, size))
            ).flatMap { content ->
                Mono.just(content.map { it.toSuccessHttpResponse() })
            }).also { log.info("get order trackings by order id: $orderId, page $page, size $size") }

    @GetMapping(path = ["/all/by-order-id/{orderId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "getAllOrderTrackingsByOrderId",
        summary = "Get all order trackings by order id",
        operationId = "getAllOrderTrackingsByOrderId",
        description = "Get all order trackings by order id"
    )
    fun getAllOrderTrackingsByOrderId(
        @PathVariable(required = true) orderId: Long
    ): ResponseEntity<Flux<SuccessOrderTrackingResponse>> =
        ResponseEntity.status(HttpStatus.OK)
            .body(orderTrackingReactiveService.getAllByOrderId(orderId)
                .flatMap { Flux.just(it.toSuccessHttpResponse()) })
            .also { log.info("get all order trackings by order id: $orderId") }

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
            .body(orderTrackingReactiveService.getLastByOrderId(orderId)
                .flatMap { Mono.just(it.toSuccessHttpResponse()) })
            .also { log.info("get last order tracking by order id: $orderId") }

    @GetMapping(path = ["/by-carrier-id/{carrierId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "getPageOrderTrackingsByCarrierId",
        summary = "Get order trackings by carrier id",
        operationId = "getPageOrderTrackingsByCarrierId",
        description = "Get order trackings by carrier id with pagination"
    )
    fun getPageOrderTrackingsByCarrierId(
        @PathVariable(required = true) carrierId: Long,
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "10") size: Int,
        @RequestParam(name = "filter-active", defaultValue = "true") filterActive: Boolean
    ): ResponseEntity<Mono<Page<SuccessOrderTrackingResponse>>> =
        ResponseEntity
            .status(HttpStatus.OK)
            .body(orderTrackingReactiveService.getPageByCarrierId(
                GetOrderTrackingsByCarrierIdDto(carrierId, PageRequest.of(page, size), filterActive)
            ).flatMap { content ->
                Mono.just(content.map { it.toSuccessHttpResponse() })
            }).also { log.info("get order trackings by carrier id: $carrierId, page $page, size $size") }

    @GetMapping(path = ["/all/by-carrier-id/{carrierId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "getAllOrderTrackingsByCarrierId",
        summary = "Get all order trackings by carrier id",
        operationId = "getAllOrderTrackingsByCarrierId",
        description = "Get all order trackings by carrier id"
    )
    fun getAllOrderTrackingsByCarrierId(
        @PathVariable(required = true) carrierId: Long,
        @RequestParam(name = "filter-active", defaultValue = "true") filterActive: Boolean
    ): ResponseEntity<Flux<SuccessOrderTrackingResponse>> =
        ResponseEntity.status(HttpStatus.OK)
            .body(orderTrackingReactiveService.getAllByCarrierId(carrierId, filterActive)
                .flatMap { Flux.just(it.toSuccessHttpResponse()) })
            .also { log.info("get all order trackings by carrier id: $carrierId") }

    @PutMapping(path = ["/status"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "setOrderTrackingStatuses",
        summary = "Set order tracking statuses",
        operationId = "setOrderTrackingStatuses",
        description = "Set order tracking statuses"
    )
    fun setOrderTrackingStatuses(
        @Valid @RequestBody req: SetOrderTrackingStatusesDto
    ): ResponseEntity<Flux<SuccessOrderTrackingResponse>> =
        ResponseEntity.status(HttpStatus.OK)
            .body(orderTrackingReactiveService.setStatuses(req)
                .flatMap { Flux.just(it.toSuccessHttpResponse()) })
            .also { log.info("set order tracking statuses: $req") }

    @PutMapping(path = ["/reorder"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "reorderOrderTrackings",
        summary = "Reorder order trackings",
        operationId = "reorderOrderTrackings",
        description = "Reorder order trackings"
    )
    fun reorderOrderTrackings(
        @Valid @RequestBody req: ReorderOrderTrackingsDto
    ): ResponseEntity<Flux<SuccessOrderTrackingResponse>> =
        ResponseEntity.status(HttpStatus.OK)
            .body(orderTrackingReactiveService.reorder(req)
                .flatMap { Flux.just(it.toSuccessHttpResponse()) })
            .also { log.info("reorder order tracking: $req") }

    @PutMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "updateOrderTracking",
        summary = "Update order tracking",
        operationId = "updateOrderTracking",
        description = "Update all single order tracking's fields"
    )
    fun updateOrderTracking(
        @Valid @RequestBody req: UpdateOrderTrackingDto
    ): ResponseEntity<Mono<SuccessOrderTrackingResponse>> =
        ResponseEntity.status(HttpStatus.OK)
            .body(orderTrackingReactiveService.update(req)
                .flatMap { Mono.just(it.toSuccessHttpResponse()) })
            .also { log.info("updated order tracking: $req") }

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
        method = "deleteByOrderTrackingExternalId",
        summary = "Delete order tracking by order id and point number",
        operationId = "deleteByOrderTrackingExternalId",
        description = "Delete order tracking by order id and point number"
    )
    fun deleteByOrderTrackingExternalId(
        @PathVariable(required = true) orderId: Long, @PathVariable(required = true) pointNumber: Int
    ): ResponseEntity<Mono<SuccessOrderTrackingResponse>> =
        ResponseEntity.status(HttpStatus.GONE)
            .body(orderTrackingReactiveService.deleteByExternalId(orderId, pointNumber)
                .flatMap { Mono.just(it.toSuccessHttpResponse()) })
            .also { log.info("deleted order tracking: $orderId, $pointNumber") }

    companion object {
        private val log = LoggerFactory.getLogger(OrderTrackingReactiveController::class.java)
    }
}
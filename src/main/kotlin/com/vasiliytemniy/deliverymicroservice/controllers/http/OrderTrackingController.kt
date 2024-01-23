package com.vasiliytemniy.deliverymicroservice.controllers.http

import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import com.vasiliytemniy.deliverymicroservice.domain.of
import com.vasiliytemniy.deliverymicroservice.domain.toSuccessHttpResponse
import com.vasiliytemniy.deliverymicroservice.dto.*
import com.vasiliytemniy.deliverymicroservice.services.OrderTrackingService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeout
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Tag(name = "Order Tracking", description = "Order Tracking REST API")
@RestController
@RequestMapping(path = ["/api/v1/order-tracking"])
class OrderTrackingController(private val orderTrackingService: OrderTrackingService) {

    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "createOrderTracking",
        summary = "Create order tracking",
        operationId = "createOrderTracking",
        description = "Create new order tracking"
    )
    suspend fun createOrderTracking(@Valid @RequestBody req: CreateOrderTrackingDto) =
        withTimeout(TIMEOUT_MILLIS) {
            ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderTrackingService.createOrderTracking(OrderTracking.of(req)).toSuccessHttpResponse())
                .also { log.info("created order tracking: $req") }
        }

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
    ) =
        withTimeout(TIMEOUT_MILLIS) {
            ResponseEntity
                .status(HttpStatus.OK)
                .body(orderTrackingService.getOrderTrackingsByOrderId(GetOrderTrackingsByOrderIdDto(orderId, PageRequest.of(page, size))))
                .also { log.info("get order trackings by order id: $orderId, page $page, size $size") }
        }

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
    ) = if (filterActive) {
            withTimeout(TIMEOUT_MILLIS) {
                ResponseEntity
                    .status(HttpStatus.OK)
                    .body(orderTrackingService.getActiveOrderTrackingsByCarrierId(GetOrderTrackingsByCarrierIdDto(carrierId, PageRequest.of(page, size))))
                    .also { log.info("get order trackings by carrier id: $carrierId, page $page, size $size") }
            }
        } else {
            withTimeout(TIMEOUT_MILLIS) {
                ResponseEntity
                    .status(HttpStatus.OK)
                    .body(orderTrackingService.getOrderTrackingsByCarrierId(GetOrderTrackingsByCarrierIdDto(carrierId, PageRequest.of(page, size))))
                    .also { log.info("get order trackings by carrier id: $carrierId, page $page, size $size") }
            }
        }

    @GetMapping(path = ["/last/{orderId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "getLastOrderTrackingByOrderId",
        summary = "Get last order tracking by order id",
        operationId = "getLastOrderTrackingByOrderId",
        description = "Get last order tracking by order id"
    )
    suspend fun getLastOrderTrackingByOrderId(@PathVariable(required = true) orderId: Long) =
        withTimeout(TIMEOUT_MILLIS) {
            ResponseEntity
                .status(HttpStatus.OK)
                .body(orderTrackingService.getLastOrderTrackingByOrderId(orderId))
                .also { log.info("get last order tracking by order id: $orderId") }
        }

    @GetMapping(path = ["/all/flow/by-order-id/{orderId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
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
    ): Flow<SuccessOrderTrackingResponse> {
        return orderTrackingService.getOrderTrackingsByOrderIdFlow(GetOrderTrackingsByOrderIdDto(orderId, PageRequest.of(page, size)))
            .map { it -> it.toSuccessHttpResponse().also { log.info("response: $it") } }
    }

    @GetMapping(path = ["/all/flow/by-carrier-id/{carrierId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "getOrderTrackingsByCarrierIdFlow",
        summary = "Get order trackings by carrier id",
        operationId = "getOrderTrackingsByCarrierIdFlow",
        description = "Get order trackings by carrier id with pagination"
    )
    fun getOrderTrackingsByCarrierIdFlow(
        @PathVariable(required = true) carrierId: Long,
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "10") size: Int
    ): Flow<SuccessOrderTrackingResponse> {
        return orderTrackingService.getOrderTrackingsByCarrierIdFlow(GetOrderTrackingsByCarrierIdDto(carrierId, PageRequest.of(page, size)))
            .map { it -> it.toSuccessHttpResponse().also { log.info("response: $it") } }
    }

    @PutMapping(path = ["/status/{orderId}/{pointNumber}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "setOrderTrackingStatus",
        summary = "Set order tracking status",
        operationId = "setOrderTrackingStatus",
        description = "Set order tracking status"
    )
    suspend fun setOrderTrackingStatus(
        @PathVariable(required = true) orderId: Long,
        @PathVariable(required = true) pointNumber: Int,
        @Valid @RequestBody req: SetOrderTrackingStatusDto
    ) = withTimeout(TIMEOUT_MILLIS) {
        ResponseEntity
            .status(HttpStatus.OK)
            .body(orderTrackingService.setOrderTrackingStatus(SetOrderTrackingStatusDto(orderId, pointNumber, req.status, req.deliveredAt)))
            .also { log.info("set order tracking status: $req") }
    }

    companion object {
        private val log = LoggerFactory.getLogger(OrderTrackingController::class.java)
        private const val TIMEOUT_MILLIS = 5000L
    }
}
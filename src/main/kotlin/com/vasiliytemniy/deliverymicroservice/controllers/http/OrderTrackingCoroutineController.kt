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
import org.springframework.data.domain.Page
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
    ): ResponseEntity<SuccessOrderTrackingResponse> =
        withTimeout(TIMEOUT_MILLIS) {
            ResponseEntity.status(HttpStatus.CREATED)
                .body(orderTrackingCoroutineService.create(OrderTracking.of(req)).toSuccessHttpResponse())
                .also { log.info("created order tracking: $req") }
        }

    @GetMapping(path = ["/flow/by-order-id/{orderId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "getFlowOrderTrackingsByOrderId",
        summary = "Get order trackings by order id",
        operationId = "getFlowOrderTrackingsByOrderId",
        description = "Get stream of all order trackings by order id"
    )
    fun getFlowOrderTrackingsByOrderId(
        @PathVariable(required = true) orderId: String,
    ): Flow<SuccessOrderTrackingResponse> =
        orderTrackingCoroutineService.getFlowByOrderId(orderId)
            .map { it.toSuccessHttpResponse() }
            .also { log.info("response: $it") }

    @GetMapping(path = ["/by-order-id/{orderId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "getPageOrderTrackingsByOrderId",
        summary = "Get order trackings by order id with pagination",
        operationId = "getPageOrderTrackingsByOrderId",
        description = "Get order trackings by order id with pagination"
    )
    suspend fun getPageOrderTrackingsByOrderId(
        @PathVariable(required = true) orderId: String,
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "10") size: Int
    ): ResponseEntity<Page<SuccessOrderTrackingResponse>> =
        withTimeout(TIMEOUT_MILLIS) {
            ResponseEntity
                .status(HttpStatus.OK)
                .body(orderTrackingCoroutineService.getPageByOrderId(
                        GetOrderTrackingsByOrderIdDto(orderId, PageRequest.of(page, size))
                    ).map { it.toSuccessHttpResponse() }
                )
                .also { log.info("response: $it") }
        }

    @GetMapping(path = ["/last/{orderId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "getLastOrderTrackingByOrderId",
        summary = "Get last order tracking by order id",
        operationId = "getLastOrderTrackingByOrderId",
        description = "Get last order tracking by order id"
    )
    suspend fun getLastOrderTrackingByOrderId(
        @PathVariable(required = true) orderId: String
    ): ResponseEntity<SuccessOrderTrackingResponse?> =
        withTimeout(TIMEOUT_MILLIS) {
            ResponseEntity
                .status(HttpStatus.OK)
                .body(orderTrackingCoroutineService.getLastByOrderId(orderId)?.toSuccessHttpResponse())
                .also { log.info("get last order tracking by order id: $orderId") }
        }

    @GetMapping(path = ["/flow/by-carrier-id/{carrierId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "getFlowOrderTrackingsByCarrierId",
        summary = "Get order trackings by carrier id",
        operationId = "getFlowOrderTrackingsByCarrierId",
        description = "Get order trackings by carrier id with pagination"
    )
    fun getFlowOrderTrackingsByCarrierId(
        @PathVariable(required = true) carrierId: String,
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "10") size: Int,
        @RequestParam(name = "filter-active", defaultValue = "true") filterActive: Boolean
    ): Flow<SuccessOrderTrackingResponse> =
        orderTrackingCoroutineService.getFlowByCarrierId(carrierId, filterActive)
            .map { it.toSuccessHttpResponse() }
            .also { log.info("response: $it") }

    @GetMapping(path = ["/by-carrier-id/{carrierId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "getPageOrderTrackingsByCarrierId",
        summary = "Get order trackings by carrier id",
        operationId = "getPageOrderTrackingsByCarrierId",
        description = "Get order trackings by carrier id with pagination"
    )
    suspend fun getPageOrderTrackingsByCarrierId(
        @PathVariable(required = true) carrierId: String,
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "10") size: Int,
        @RequestParam(name = "filter-active", defaultValue = "true") filterActive: Boolean
    ): ResponseEntity<Page<SuccessOrderTrackingResponse>> =
        withTimeout(TIMEOUT_MILLIS) {
            ResponseEntity
                .status(HttpStatus.OK)
                .body(orderTrackingCoroutineService.getPageByCarrierId(
                    GetOrderTrackingsByCarrierIdDto(carrierId, PageRequest.of(page, size), filterActive)
                ).map { it.toSuccessHttpResponse() })
                .also { log.info("response: $it") }
        }

    @PutMapping(path = ["/status"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "setOrderTrackingStatuses",
        summary = "Set order tracking statuses",
        operationId = "setOrderTrackingStatuses",
        description = "Set order tracking statuses"
    )
    suspend fun setOrderTrackingStatuses(
        @RequestBody req: Any
    ) : ResponseEntity<List<SuccessOrderTrackingResponse>> =
        withTimeout(TIMEOUT_MILLIS) {
            ResponseEntity
                .status(HttpStatus.OK)
                .body(orderTrackingCoroutineService.setStatuses(
                    SetOrderTrackingStatusesDto.of(req)
                ).map { it.toSuccessHttpResponse() }
            ).also { log.info("set order tracking status: $req") }
        }

    @PutMapping(path = ["/reorder"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "reorderOrderTrackings",
        summary = "Reorder order trackings",
        operationId = "reorderOrderTrackings",
        description = "Reorder order trackings, change point numbers"
    )
    suspend fun reorderOrderTrackings(
        @Valid @RequestBody req: ReorderOrderTrackingsDto
    ) : ResponseEntity<List<SuccessOrderTrackingResponse>> =
        withTimeout(TIMEOUT_MILLIS) {
            ResponseEntity
                .status(HttpStatus.OK)
                .body(orderTrackingCoroutineService.reorder(
                        ReorderOrderTrackingsDto(req.orderId, req.fromPointNumberToPointNumber)
                    ).map { it.toSuccessHttpResponse() }
                ).also { log.info("reorder order trackings: $req") }
        }

    @PutMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "updateOrderTracking",
        summary = "Update order tracking",
        operationId = "updateOrderTracking",
        description = "Update order tracking"
    )
    suspend fun updateOrderTracking(
        @Valid @RequestBody req: UpdateOrderTrackingDto
    ): ResponseEntity<SuccessOrderTrackingResponse> =
        withTimeout(TIMEOUT_MILLIS) {
            ResponseEntity
                .status(HttpStatus.OK)
                .body(orderTrackingCoroutineService.update(req)?.toSuccessHttpResponse())
                .also { log.info("updated order tracking: $req") }
        }

    @DeleteMapping(path = ["/{orderId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "deleteAllByOrderId",
        summary = "Delete all order trackings by order id",
        operationId = "deleteAllByOrderId",
        description = "Delete all order trackings by order id"
    )
    suspend fun deleteAllByOrderId(
        @PathVariable(required = true) orderId: String
    ): ResponseEntity<List<SuccessOrderTrackingResponse>> =
        withTimeout(TIMEOUT_MILLIS) {
            ResponseEntity
                .status(HttpStatus.GONE)
                .body(orderTrackingCoroutineService.deleteAllByOrderId(orderId).map { it.toSuccessHttpResponse() })
                .also { log.info("deleted order tracking: $orderId") }
        }

    @DeleteMapping(path = ["/{orderId}/{pointNumber}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "deleteByOrderTrackingExternalId",
        summary = "Delete order tracking by order id and point number",
        operationId = "deleteByOrderTrackingExternalId",
        description = "Delete order tracking by order id and point number"
    )
    suspend fun deleteByOrderTrackingExternalId(
        @PathVariable(required = true) orderId: String,
        @PathVariable(required = true) pointNumber: Int
    ): ResponseEntity<SuccessOrderTrackingResponse> =
        withTimeout(TIMEOUT_MILLIS) {
            ResponseEntity
                .status(HttpStatus.GONE)
                .body(orderTrackingCoroutineService.deleteByExternalId(orderId, pointNumber)?.toSuccessHttpResponse())
                .also { log.info("deleted order tracking: $orderId, $pointNumber") }
        }

    companion object {
        private val log = LoggerFactory.getLogger(OrderTrackingReactiveController::class.java)
        private const val TIMEOUT_MILLIS = 5000L
    }
}
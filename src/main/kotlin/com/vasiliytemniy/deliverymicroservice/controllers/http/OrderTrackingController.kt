package com.vasiliytemniy.deliverymicroservice.controllers.http

import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import com.vasiliytemniy.deliverymicroservice.domain.of
import com.vasiliytemniy.deliverymicroservice.domain.toSuccessHttpResponse
import com.vasiliytemniy.deliverymicroservice.dto.CreateOrderTrackingDto
import com.vasiliytemniy.deliverymicroservice.services.OrderTrackingService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.withTimeout
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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

    companion object {
        private val log = LoggerFactory.getLogger(OrderTrackingController::class.java)
        private const val TIMEOUT_MILLIS = 5000L
    }
}
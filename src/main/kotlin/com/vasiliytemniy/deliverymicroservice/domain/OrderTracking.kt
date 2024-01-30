package com.vasiliytemniy.deliverymicroservice.domain

import com.vasiliytemniy.deliverymicroservice.dto.CreateOrderTrackingDto
import com.vasiliytemniy.deliverymicroservice.dto.SuccessOrderTrackingResponse
import com.vasiliytemniy.deliverymicroservice.utils.parseOptionalDate
import jakarta.validation.constraints.Size
import com.vasiliytemniy.grpc.ordertracking.service.OrderTracking.OrderTrackingData as OrderTrackingDataGrpc
import com.vasiliytemniy.grpc.ordertracking.service.OrderTracking.CreateRequest as CreateOrderTrackingRequestGrpc
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*



@Table(schema = "delivery", name = "order_trackings")
data class OrderTracking(
    @Column(ID) @Id var id: UUID?,
    @Column(ORDER_ID) var orderId: String = "",
    @Column(POINT_NUMBER) var pointNumber: Int? = null,
    @Column(FROM_FACILITY_ID) var fromFacilityId: String = "",
    @Column(DESTINATION_ID) var destinationId: String = "",
    @get:Size(min = 3, max = 60) @Column(DESTINATION_TYPE) var destinationType: String = "",
    @Column(CARRIER_ID) var carrierId: String = "",
    @get:Size(min = 3, max = 60) @Column(STATUS) var status: String = "",
    @Column(DELIVERY_COST) var deliveryCost: Int = 0,
    @get:Size(min = 3, max = 3) @Column(CURRENCY) var currency: String = "",
    @Column(CURRENCY_DECIMAL_MULTIPLIER) var currencyDecimalMultiplier: Int = 1,
    @Column(MASS_CONTROL_VALUE) var massControlValue: Int? = null,
    @get:Size(min = 1, max = 60) @Column(MASS_MEASURE) var massMeasure: String? = null,
    @Column(LAT) var lat: Double? = null,
    @Column(LON) var lon: Double? = null,
    @Column(ESTIMATED_DELIVERY_AT) var estimatedDeliveryAt: LocalDateTime? = null,
    @Column(DELIVERED_AT) var deliveredAt: LocalDateTime? = null,
    @Column(CREATED_AT) var createdAt: LocalDateTime? = null,
    @Column(UPDATED_AT) var updatedAt: LocalDateTime? = null
) {

    companion object {
        const val ID = "id"
        const val ORDER_ID = "order_id"
        const val POINT_NUMBER = "point_number"
        const val FROM_FACILITY_ID = "from_facility_id"
        const val DESTINATION_ID = "destination_id"
        const val DESTINATION_TYPE = "destination_type"
        const val CARRIER_ID = "carrier_id"
        const val STATUS = "status"
        const val DELIVERY_COST = "delivery_cost"
        const val CURRENCY = "currency"
        const val CURRENCY_DECIMAL_MULTIPLIER = "currency_decimal_multiplier"
        const val MASS_CONTROL_VALUE = "mass_control_value"
        const val MASS_MEASURE = "mass_measure"
        const val LAT = "lat"
        const val LON = "lon"
        const val ESTIMATED_DELIVERY_AT = "estimated_delivery_at"
        const val DELIVERED_AT = "delivered_at"
        const val CREATED_AT = "created_at"
        const val UPDATED_AT = "updated_at"
    }
}

fun OrderTracking.toProto(): OrderTrackingDataGrpc {
    // Set required fields
    val response = OrderTrackingDataGrpc.newBuilder()
        .setId(this.id.toString())
        .setOrderId(this.orderId)
        .setPointNumber(this.pointNumber?:0)
        .setFromFacilityId(this.fromFacilityId)
        .setDestinationId(this.destinationId)
        .setDestinationType(this.destinationType)
        .setCarrierId(this.carrierId)
        .setStatus(this.status)
        .setDeliveryCost(this.deliveryCost)
        .setCurrency(this.currency)
        .setCurrencyDecimalMultiplier(this.currencyDecimalMultiplier)

    // Set optional fields
    this.massControlValue?.let { response.setMassControlValue(it) }
    this.massMeasure?.let { response.setMassMeasure(it) }
    this.lat?.let { response.setLat(it) }
    this.lon?.let { response.setLon(it) }
    this.estimatedDeliveryAt?.let { response.setEstimatedDeliveryAt(it.toString()) }
    this.deliveredAt?.let { response.setDeliveredAt(it.toString()) }
    this.createdAt?.let { response.setCreatedAt(it.toString()) }
    this.updatedAt?.let { response.setUpdatedAt(it.toString()) }

    return response.build()
}

fun OrderTracking.toSuccessHttpResponse(): SuccessOrderTrackingResponse {
    return SuccessOrderTrackingResponse(
        id = this.id,
        orderId = this.orderId,
        pointNumber = this.pointNumber,
        fromFacilityId = this.fromFacilityId,
        destinationId = this.destinationId,
        destinationType = this.destinationType,
        carrierId = this.carrierId,
        status = this.status,
        deliveryCost = this.deliveryCost,
        currency = this.currency,
        currencyDecimalMultiplier = this.currencyDecimalMultiplier,
        massControlValue = this.massControlValue,
        massMeasure = this.massMeasure,
        lat = this.lat,
        lon = this.lon,
        estimatedDeliveryAt = this.estimatedDeliveryAt?.toString(),
        deliveredAt = this.deliveredAt?.toString(),
        createdAt = this.createdAt.toString(),
        updatedAt = this.updatedAt.toString()
    )
}

fun OrderTracking.Companion.of(request: CreateOrderTrackingRequestGrpc): OrderTracking {
    return OrderTracking(
        id = null,
        orderId = request.orderId,
        pointNumber = request.pointNumber,
        fromFacilityId = request.fromFacilityId,
        destinationId = request.destinationId,
        destinationType = request.destinationType,
        carrierId = request.carrierId,
        status = request.status,
        deliveryCost = request.deliveryCost,
        currency = request.currency,
        currencyDecimalMultiplier = request.currencyDecimalMultiplier,
        massControlValue = request.massControlValue,
        massMeasure = request.massMeasure,
        lat = request.lat,
        lon = request.lon,
        estimatedDeliveryAt = parseOptionalDate(request.estimatedDeliveryAt),
        deliveredAt = parseOptionalDate(request.deliveredAt),
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
}

fun OrderTracking.Companion.of(request: CreateOrderTrackingDto): OrderTracking {
    return OrderTracking(
        id = null,
        orderId = request.orderId,
        pointNumber = request.pointNumber,
        fromFacilityId = request.fromFacilityId,
        destinationId = request.destinationId,
        destinationType = request.destinationType,
        carrierId = request.carrierId,
        status = request.status,
        deliveryCost = request.deliveryCost,
        currency = request.currency,
        currencyDecimalMultiplier = request.currencyDecimalMultiplier,
        massControlValue = request.massControlValue,
        massMeasure = request.massMeasure,
        lat = request.lat,
        lon = request.lon,
        estimatedDeliveryAt = parseOptionalDate(request.estimatedDeliveryAt),
        deliveredAt = parseOptionalDate(request.deliveredAt),
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
}


/**
 * Transform DB row to OrderTracking
 *
 * Asserts that row contains all required fields
 */
fun OrderTracking.Companion.of(row: Map<String, Any>): OrderTracking {
    return OrderTracking(
        id = row["id"] as UUID,
        orderId = row["order_id"] as String,
        pointNumber = row["point_number"] as Int,
        fromFacilityId = row["from_facility_id"] as String,
        destinationId = row["destination_id"] as String,
        destinationType = row["destination_type"] as String,
        carrierId = row["carrier_id"] as String,
        status = row["status"] as String,
        deliveryCost = row["delivery_cost"] as Int,
        currency = row["currency"] as String,
        currencyDecimalMultiplier = row["currency_decimal_multiplier"] as Int,
        massControlValue = row["mass_control_value"] as Int?,
        massMeasure = row["mass_measure"] as String?,
        lat = row["lat"] as Double?,
        lon = row["lon"] as Double?,
        deliveredAt = row["delivered_at"] as LocalDateTime?,
        createdAt = row["created_at"] as LocalDateTime?,
        updatedAt = row["updated_at"] as LocalDateTime?
    )
}
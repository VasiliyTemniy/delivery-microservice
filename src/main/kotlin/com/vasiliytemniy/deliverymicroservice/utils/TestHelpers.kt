package com.vasiliytemniy.deliverymicroservice.utils

import com.github.javafaker.Faker
import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import com.vasiliytemniy.deliverymicroservice.repositories.OrderTrackingCoroutineRepository
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import kotlin.math.pow


/**
 * Used for test service method and for unit tests
 */
fun generateOrderTracking(
    faker: Faker,
    overrideIsDelivered: Boolean? = null,
    overrideHasMassControl: Boolean? = null,
    overrideMassControlValue: Int? = null,
    overrideMassMeasure: String? = null,
    overrideOrderIdNumber: String? = null,
    overridePointNumber: Int? = null,
    overrideFromFacilityId: String? = null,
    overrideDestinationId: String? = null,
    overrideCurrency: String? = null,
    overrideCurrencyMultiplier: Int? = null
): OrderTracking {
    val isDelivered = overrideIsDelivered?:faker.options().option(true, false)

    val hasMassControl = overrideHasMassControl?:faker.options().option(true, false)

    val createdAtDate = faker.date().past(6, 3, TimeUnit.DAYS)
    val createdAt = createdAtDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
    val updatedAt = faker.date().future(3, TimeUnit.DAYS, createdAtDate).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()

    return OrderTracking(
        id = null,
        orderId = overrideOrderIdNumber?:faker.number().numberBetween(1, 1000).toString(),
        pointNumber = overridePointNumber?:faker.number().numberBetween(1, 1000),
        fromFacilityId = overrideFromFacilityId?:faker.number().numberBetween(1, 1000).toString(),
        destinationId = overrideDestinationId?:faker.number().numberBetween(1, 1000).toString(),
        destinationType = faker.options().option("facility", "warehouse", "store", "customer"),
        carrierId = faker.number().numberBetween(1, 1000).toString(),
        status = if (isDelivered) "delivered" else faker.options().option("sorting", "inTransit", "loading", "unloading"),
        deliveryCost = faker.number().numberBetween(1, 1000),
        currency = overrideCurrency?:faker.currency().code(),
        currencyDecimalMultiplier = overrideCurrencyMultiplier?:10.toFloat().pow(faker.number().numberBetween(1, 3)).toInt(),
        massControlValue = overrideMassControlValue?: if (hasMassControl) faker.number().numberBetween(1, 1000) else null,
        massMeasure = overrideMassMeasure?: if (hasMassControl) faker.options().option( "kg", "g", "t") else null,
        estimatedDeliveryAt = if (isDelivered)
            faker.date().past(3, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        else
            faker.date().future(3, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
        deliveredAt = if (isDelivered) faker.date().past(3, TimeUnit.DAYS).toInstant().atZone(
            ZoneId.systemDefault()).toLocalDateTime() else null,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
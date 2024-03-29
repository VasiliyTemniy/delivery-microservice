package com.vasiliytemniy.deliverymicroservice

import com.github.javafaker.Faker
import com.vasiliytemniy.deliverymicroservice.repositories.OrderTrackingCoroutineRepository
import com.vasiliytemniy.deliverymicroservice.repositories.OrderTrackingReactiveRepository
import com.vasiliytemniy.deliverymicroservice.utils.generateOrderTracking
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*


@SpringBootTest
class OrderTrackingRepositoryTests {

    @Autowired
    lateinit var orderTrackingCoroutineRepository: OrderTrackingCoroutineRepository

    @Autowired
    lateinit var orderTrackingReactiveRepository: OrderTrackingReactiveRepository

    var faker = Faker(Locale.of("en"))

    @Test
    fun `should save order tracking`() = runBlocking {
        orderTrackingCoroutineRepository.deleteAll()

        val generatedOrderTracking = generateOrderTracking(faker)

        val reactiveResult = orderTrackingReactiveRepository.save(
            generatedOrderTracking
        ).block()
        assert(reactiveResult != null) { "Saved order tracking should not be null" }

        orderTrackingReactiveRepository.deleteAll()

        val coroutineResult = orderTrackingCoroutineRepository.save(generatedOrderTracking)

        assert(coroutineResult == reactiveResult)
    }

    @Test
    fun `should find order tracking by external id`() = runBlocking {
        orderTrackingCoroutineRepository.deleteAll()

        val savedOrderTracking = orderTrackingCoroutineRepository.save(generateOrderTracking(faker))

        assert(savedOrderTracking.pointNumber != null) { "Point number should not be null" }

        val foundCoroutineOrderTracking = orderTrackingCoroutineRepository.findByOrderIdAndPointNumber(
            savedOrderTracking.orderId,
            savedOrderTracking.pointNumber!!
        )

        assert(foundCoroutineOrderTracking != null) { "Found order tracking should not be null" }
        assert(savedOrderTracking == foundCoroutineOrderTracking) { "Found order tracking should be the same as saved order tracking" }

        val foundReactiveOrderTracking = orderTrackingReactiveRepository.findByOrderIdAndPointNumber(
            savedOrderTracking.orderId,
            savedOrderTracking.pointNumber!!
        ).block()

        assert(foundReactiveOrderTracking != null) { "Found order tracking should not be null" }
        assert(savedOrderTracking == foundReactiveOrderTracking) { "Found order tracking should be the same as saved order tracking" }
    }

    @Test
    fun `should update order tracking`() = runBlocking {
        val originalOrderTracking = generateOrderTracking(faker)

        val updatedOrderTracking = generateOrderTracking(
            faker,
            overrideOrderIdNumber = originalOrderTracking.orderId,
            overridePointNumber = originalOrderTracking.pointNumber
        )

        orderTrackingCoroutineRepository.deleteAll()

        orderTrackingCoroutineRepository.save(originalOrderTracking)
        val coroutineResult = orderTrackingCoroutineRepository.update(
            originalOrderTracking.orderId,
            originalOrderTracking.pointNumber ?: fail("Point number was not generated by generateOrderTracking"),
            updatedOrderTracking.fromFacilityId,
            updatedOrderTracking.destinationId,
            updatedOrderTracking.destinationType,
            updatedOrderTracking.carrierId,
            updatedOrderTracking.status,
            updatedOrderTracking.deliveryCost,
            updatedOrderTracking.currency,
            updatedOrderTracking.currencyDecimalMultiplier,
            updatedOrderTracking.massControlValue,
            updatedOrderTracking.massMeasure,
            updatedOrderTracking.estimatedDeliveryAt,
            updatedOrderTracking.deliveredAt,
        ) ?: fail("Order tracking was not saved - coroutine repository test")

        assert(coroutineResult.orderId == updatedOrderTracking.orderId)
        assert(coroutineResult.pointNumber == updatedOrderTracking.pointNumber)
        assert(coroutineResult.fromFacilityId == updatedOrderTracking.fromFacilityId)
        assert(coroutineResult.destinationId == updatedOrderTracking.destinationId)
        assert(coroutineResult.destinationType == updatedOrderTracking.destinationType)
        assert(coroutineResult.carrierId == updatedOrderTracking.carrierId)
        assert(coroutineResult.status == updatedOrderTracking.status)
        assert(coroutineResult.deliveryCost == updatedOrderTracking.deliveryCost)
        assert(coroutineResult.currency == updatedOrderTracking.currency)
        assert(coroutineResult.currencyDecimalMultiplier == updatedOrderTracking.currencyDecimalMultiplier)
        assert(coroutineResult.massControlValue == updatedOrderTracking.massControlValue)
        assert(coroutineResult.massMeasure == updatedOrderTracking.massMeasure)
        assert(coroutineResult.estimatedDeliveryAt == updatedOrderTracking.estimatedDeliveryAt)
        assert(coroutineResult.deliveredAt == updatedOrderTracking.deliveredAt)


        orderTrackingReactiveRepository.deleteAll()

        orderTrackingReactiveRepository.save(originalOrderTracking)
        val reactiveResult = orderTrackingReactiveRepository.update(
            originalOrderTracking.orderId,
            originalOrderTracking.pointNumber ?: fail("Point number was not generated by generateOrderTracking"),
            updatedOrderTracking.fromFacilityId,
            updatedOrderTracking.destinationId,
            updatedOrderTracking.destinationType,
            updatedOrderTracking.carrierId,
            updatedOrderTracking.status,
            updatedOrderTracking.deliveryCost,
            updatedOrderTracking.currency,
            updatedOrderTracking.currencyDecimalMultiplier,
            updatedOrderTracking.massControlValue,
            updatedOrderTracking.massMeasure,
            updatedOrderTracking.estimatedDeliveryAt,
            updatedOrderTracking.deliveredAt,
        ).block() ?: fail("Order tracking was not saved - reactive repository test")

        assert(reactiveResult.orderId == updatedOrderTracking.orderId)
        assert(reactiveResult.pointNumber == updatedOrderTracking.pointNumber)
        assert(reactiveResult.fromFacilityId == updatedOrderTracking.fromFacilityId)
        assert(reactiveResult.destinationId == updatedOrderTracking.destinationId)
        assert(reactiveResult.destinationType == updatedOrderTracking.destinationType)
        assert(reactiveResult.carrierId == updatedOrderTracking.carrierId)
        assert(reactiveResult.status == updatedOrderTracking.status)
        assert(reactiveResult.deliveryCost == updatedOrderTracking.deliveryCost)
        assert(reactiveResult.currency == updatedOrderTracking.currency)
        assert(reactiveResult.currencyDecimalMultiplier == updatedOrderTracking.currencyDecimalMultiplier)
        assert(reactiveResult.massControlValue == updatedOrderTracking.massControlValue)
        assert(reactiveResult.massMeasure == updatedOrderTracking.massMeasure)
        assert(reactiveResult.estimatedDeliveryAt == updatedOrderTracking.estimatedDeliveryAt)
        assert(reactiveResult.deliveredAt == updatedOrderTracking.deliveredAt)
    }
}
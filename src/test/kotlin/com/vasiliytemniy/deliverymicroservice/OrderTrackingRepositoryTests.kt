package com.vasiliytemniy.deliverymicroservice

import com.github.javafaker.Faker
import com.vasiliytemniy.deliverymicroservice.repositories.OrderTrackingCoroutineRepository
import com.vasiliytemniy.deliverymicroservice.repositories.OrderTrackingReactiveRepository
import com.vasiliytemniy.deliverymicroservice.utils.generateOrderTracking
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
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
    fun `should save order tracking`() {
        runBlocking {
            orderTrackingCoroutineRepository.deleteAll()

            val savedReactiveOrderTracking = orderTrackingReactiveRepository.save(
                generateOrderTracking(faker)
            ).block()
            assert(savedReactiveOrderTracking != null) { "Saved order tracking should not be null" }

            orderTrackingCoroutineRepository.save(generateOrderTracking(faker))
        }
    }

    @Test
    fun `should find order tracking by external id`() {
        runBlocking {
            orderTrackingCoroutineRepository.deleteAll()

            val savedOrderTracking = orderTrackingCoroutineRepository.save(generateOrderTracking(faker))

            assert(savedOrderTracking.pointNumber != null) { "Point number should not be null" }

            val foundCoroutineOrderTracking = orderTrackingCoroutineRepository.findByOrderIdAndPointNumber(
                savedOrderTracking.orderId,
                savedOrderTracking.pointNumber!!
            )

            println("savedOrderTracking = $savedOrderTracking")
            println("foundCoroutineOrderTracking = $foundCoroutineOrderTracking")

            assert(foundCoroutineOrderTracking != null) { "Found order tracking should not be null" }
            assert(savedOrderTracking == foundCoroutineOrderTracking) { "Found order tracking should be the same as saved order tracking" }

            val foundReactiveOrderTracking = orderTrackingReactiveRepository.findByOrderIdAndPointNumber(
                savedOrderTracking.orderId,
                savedOrderTracking.pointNumber!!
            ).block()

            assert(foundReactiveOrderTracking != null) { "Found order tracking should not be null" }
            assert(savedOrderTracking == foundReactiveOrderTracking) { "Found order tracking should be the same as saved order tracking" }
        }
    }
}
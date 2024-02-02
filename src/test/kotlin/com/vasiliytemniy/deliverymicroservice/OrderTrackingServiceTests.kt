package com.vasiliytemniy.deliverymicroservice

import com.github.javafaker.Faker
import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import com.vasiliytemniy.deliverymicroservice.domain.of
import com.vasiliytemniy.deliverymicroservice.dto.GetOrderTrackingsByOrderIdDto
import com.vasiliytemniy.deliverymicroservice.repositories.OrderTrackingCoroutineRepository
import com.vasiliytemniy.deliverymicroservice.repositories.OrderTrackingReactiveRepository
import com.vasiliytemniy.deliverymicroservice.services.OrderTrackingCoroutineService
import com.vasiliytemniy.deliverymicroservice.services.OrderTrackingCoroutineServiceImpl
import com.vasiliytemniy.deliverymicroservice.services.OrderTrackingReactiveService
import com.vasiliytemniy.deliverymicroservice.services.OrderTrackingReactiveServiceImpl
import com.vasiliytemniy.deliverymicroservice.utils.generateOrderTracking
import com.vasiliytemniy.deliverymicroservice.dto.OrderTrackingExternalIdDto
import com.vasiliytemniy.deliverymicroservice.dto.SetOrderTrackingStatusesDto
import com.vasiliytemniy.deliverymicroservice.dto.UpdateOrderTrackingDto
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit


class OrderTrackingServiceTests {

    private val faker = Faker(Locale.of("en"))

    private val orderTrackingCoroutineRepository: OrderTrackingCoroutineRepository = mockk()
    private val orderTrackingReactiveRepository: OrderTrackingReactiveRepository = mockk()

    private val orderTrackingCoroutineService: OrderTrackingCoroutineService = OrderTrackingCoroutineServiceImpl(
        orderTrackingCoroutineRepository
    )

    private val orderTrackingReactiveService: OrderTrackingReactiveService = OrderTrackingReactiveServiceImpl(
        orderTrackingReactiveRepository
    )

    @Test
    fun `should create order tracking`() = runBlocking {
        val orderTracking: OrderTracking = generateOrderTracking(faker)

        every { runBlocking { orderTrackingCoroutineRepository.save(orderTracking) } } returns orderTracking
        every { orderTrackingReactiveRepository.save(orderTracking).block() } returns orderTracking

        val coroutineResult = orderTrackingCoroutineService.create(orderTracking)
        val reactiveResult = orderTrackingReactiveService.create(orderTracking).block()

        verify(exactly = 1) { runBlocking { orderTrackingCoroutineRepository.save(orderTracking) } }
        verify(exactly = 1) { orderTrackingReactiveRepository.save(orderTracking).block() }
        assert(orderTracking == coroutineResult) { "Coroutine create test failed" }
        assert(orderTracking == reactiveResult) { "Reactive create test failed" }
    }

    @Test
    fun `should get page by order id`() = runBlocking {
        val orderId = "1"

        val orderTrackingList = (1..10).map {
            generateOrderTracking(faker, overrideOrderIdNumber = orderId)
        }

        val pageable = PageRequest.of(0, 4)

        val orderTrackingPageOf4 = PageImpl(
            orderTrackingList, pageable, 10L
        )

        every { runBlocking { orderTrackingCoroutineRepository.findPageByOrderId(orderId, pageable) } } returns
                orderTrackingPageOf4
        every { orderTrackingReactiveRepository.findPageByOrderId(orderId, pageable) } returns
                Flux.fromIterable(orderTrackingList)
        every { orderTrackingReactiveRepository.countByOrderId(orderId) } returns
                Mono.just(10)

        val coroutineResult = orderTrackingCoroutineService.getPageByOrderId(
            GetOrderTrackingsByOrderIdDto(orderId, pageable)
        )
        val reactiveResult = orderTrackingReactiveService.getPageByOrderId(
            GetOrderTrackingsByOrderIdDto(orderId, pageable)
        ).block()

        verify(exactly = 1) { runBlocking { orderTrackingCoroutineRepository.findPageByOrderId(orderId, pageable) } }
        verify(exactly = 1) { orderTrackingReactiveRepository.findPageByOrderId(orderId, pageable) }
        assert(orderTrackingPageOf4 == coroutineResult)
        assert(orderTrackingPageOf4 == reactiveResult)
    }

    @Test
    fun `should set statuses`() = runBlocking {
            val testObjectsCount = 10

        // Will be used to generate OrderTrackings and to pass to service as an argument
        val orderTrackingExternalIds = (1..testObjectsCount).map {
            OrderTrackingExternalIdDto(
                faker.number().numberBetween(1, 1000).toString(),
                faker.number().numberBetween(1, 1000),
            )
        }

        val orderTrackingList = orderTrackingExternalIds.map {
            generateOrderTracking(faker, overrideOrderIdNumber = it.orderId, overridePointNumber = it.pointNumber)
        }

        val pickedStatus = faker.options().option("sorting", "inTransit", "loading", "unloading")

        val pickedIsDelivered = faker.options().option(true, false)

        val pickedDeliveredAt =
            if (pickedIsDelivered)
                faker.date().past(3, TimeUnit.DAYS).toInstant().atZone(
                    ZoneId.systemDefault()
                ).toLocalDateTime()
            else null

        val updatedOrderTrackingList = orderTrackingList.map {
            OrderTracking(
                it.id,
                it.orderId,
                it.pointNumber,
                it.fromFacilityId,
                it.destinationId,
                it.destinationType,
                it.carrierId,
                pickedStatus,
                it.deliveryCost,
                it.currency,
                it.currencyDecimalMultiplier,
                it.massControlValue,
                it.massMeasure,
                it.estimatedDeliveryAt,
                pickedDeliveredAt,
                it.createdAt,
                it.updatedAt
            )
        }

        updatedOrderTrackingList.map {
            every {
                runBlocking {
                    orderTrackingCoroutineRepository.setStatus(
                        it.orderId,
                        it.pointNumber ?: fail("Point number was not generated by generateOrderTracking"),
                        pickedStatus,
                        pickedDeliveredAt
                    )
                }
            } returns it

            every {
                orderTrackingReactiveRepository.setStatus(
                    it.orderId,
                    it.pointNumber ?: fail("Point number was not generated by generateOrderTracking"),
                    pickedStatus,
                    pickedDeliveredAt
                )
            } returns Mono.just(it)
        }

        val coroutineResult = orderTrackingCoroutineService.setStatuses(
            SetOrderTrackingStatusesDto(
                orderTrackingExternalIds, pickedStatus, pickedDeliveredAt.toString()
            )
        )
        val reactiveResult = orderTrackingReactiveService.setStatuses(
            SetOrderTrackingStatusesDto(
                orderTrackingExternalIds, pickedStatus, pickedDeliveredAt.toString()
            )
        ).collectList().block()?.toList() ?: fail("Reactive service returned null for setStatuses")

        orderTrackingExternalIds.map {
            verify(exactly = 1) {
                runBlocking {
                    orderTrackingCoroutineRepository.setStatus(
                        it.orderId, it.pointNumber, pickedStatus, pickedDeliveredAt
                    )
                }
            }
            verify(exactly = 1) {
                orderTrackingReactiveRepository.setStatus(
                    it.orderId, it.pointNumber, pickedStatus, pickedDeliveredAt
                )
            }
        }

        assert(updatedOrderTrackingList == coroutineResult)
        assert(updatedOrderTrackingList == reactiveResult)
    }

    @Test
    fun `should perfom full update for order tracking`() = runBlocking {
            val originalOrderTracking: OrderTracking = generateOrderTracking(faker)

        val updatedOrderTracking: OrderTracking = generateOrderTracking(
            faker,
            overrideOrderIdNumber = originalOrderTracking.orderId,
            overridePointNumber = originalOrderTracking.pointNumber
        )

        val dto = UpdateOrderTrackingDto(
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
            updatedOrderTracking.estimatedDeliveryAt.toString(),
            updatedOrderTracking.deliveredAt.toString(),
        )

        every {
            runBlocking {
                orderTrackingCoroutineRepository.update(
                    originalOrderTracking.orderId,
                    originalOrderTracking.pointNumber
                        ?: fail("Point number was not generated by generateOrderTracking"),
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
                )
            }
        } returns updatedOrderTracking

        every {
            orderTrackingReactiveRepository.update(
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
            )
        } returns Mono.just(updatedOrderTracking)

        val coroutineResult = orderTrackingCoroutineService.update(dto)

        val reactiveResult = orderTrackingReactiveService.update(dto).block()

        verify(exactly = 1) {
            runBlocking {
                orderTrackingCoroutineRepository.update(
                    originalOrderTracking.orderId,
                    originalOrderTracking.pointNumber
                        ?: fail("Point number was not generated by generateOrderTracking"),
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
                )
            }
        }

        verify(exactly = 1) {
            orderTrackingReactiveRepository.update(
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
            )
        }

        assert(updatedOrderTracking == coroutineResult)
        assert(updatedOrderTracking == reactiveResult)
    }
}
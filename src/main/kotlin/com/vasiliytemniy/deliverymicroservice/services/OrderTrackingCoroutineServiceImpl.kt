package com.vasiliytemniy.deliverymicroservice.services

import com.github.javafaker.Faker
import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import com.vasiliytemniy.deliverymicroservice.dto.*
import com.vasiliytemniy.deliverymicroservice.exceptions.OrderTrackingNotFoundException
import com.vasiliytemniy.deliverymicroservice.repositories.OrderTrackingCoroutineRepository
import com.vasiliytemniy.deliverymicroservice.utils.generateOrderTracking
import jakarta.validation.Valid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.pow

@Service
class OrderTrackingCoroutineServiceImpl(
    private val orderTrackingCoroutineRepository: OrderTrackingCoroutineRepository,
    private val faker: Faker
) : OrderTrackingCoroutineService {

    @Transactional
    override suspend fun create(@Valid orderTracking: OrderTracking): OrderTracking =
        withContext(Dispatchers.IO) {
            orderTrackingCoroutineRepository.save(orderTracking)
        }

    @Transactional(readOnly = true)
    override fun getFlowByOrderId(orderId: String): Flow<OrderTracking> =
        orderTrackingCoroutineRepository.findAllByOrderId(orderId)

    @Transactional(readOnly = true)
    override suspend fun getPageByOrderId(requestDto: GetOrderTrackingsByOrderIdDto): Page<OrderTracking> =
        withContext(Dispatchers.IO) {
            orderTrackingCoroutineRepository.findPageByOrderId(requestDto.orderId, requestDto.pageable)
        }

    @Transactional(readOnly = true)
    override suspend fun getLastByOrderId(orderId: String): OrderTracking? =
        withContext(Dispatchers.IO) {
            orderTrackingCoroutineRepository.findLastByOrderId(orderId)
        }

    @Transactional(readOnly = true)
    override fun getFlowByCarrierId(carrierId: String, filterActive: Boolean): Flow<OrderTracking> =
        orderTrackingCoroutineRepository.findAllByCarrierId(carrierId, filterActive)

    @Transactional(readOnly = true)
    override suspend fun getPageByCarrierId(requestDto: GetOrderTrackingsByCarrierIdDto): Page<OrderTracking> =
        withContext(Dispatchers.IO) {
            orderTrackingCoroutineRepository.findPageByCarrierId(requestDto.carrierId, requestDto.pageable, requestDto.filterActive)
        }

    // TODO: consider implementing with single SQL query
    @Transactional
    override suspend fun setStatuses(requestDto: SetOrderTrackingStatusesDto): List<OrderTracking> =
        withContext(Dispatchers.IO) {
            requestDto.orderTrackingExternalIds.mapNotNull {
                orderTrackingCoroutineRepository.setStatus(
                    it.orderId,
                    it.pointNumber,
                    requestDto.status,
                    requestDto.parsedDeliveredAt
                )
            }
        }

    @Transactional
    override suspend fun reorder(requestDto: ReorderOrderTrackingsDto, performRequestCheck: Boolean): List<OrderTracking> =
        withContext(Dispatchers.IO) {

            // Perform optional check of requestDto fromPointNumberToPointNumber Map
            if (performRequestCheck)
                if (!checkReorderRequestDto(requestDto))
                    throw IllegalArgumentException("Duplicate or invalid point numbers")

            val deletedOrderTrackings = mutableListOf<OrderTracking>()

            // Delete all order trackings with specified as "from" point numbers
            requestDto.fromPointNumberToPointNumber.forEach {
                val deletedOrderTracking = orderTrackingCoroutineRepository
                    .deleteByExternalId(requestDto.orderId, it.key)?: throw OrderTrackingNotFoundException(
                        "Order tracking with order id ${requestDto.orderId} and point number ${it.key} not found",
                        "orderId"
                    )

                deletedOrderTrackings.add(deletedOrderTracking)
            }

            // Save all order trackings with specified as "to" point numbers, return result
            deletedOrderTrackings.map {
                orderTrackingCoroutineRepository.save(
                    it.copy(
                        pointNumber = requestDto.fromPointNumberToPointNumber[it.pointNumber]
                    )
                )
            }
    }

    private fun checkReorderRequestDto(requestDto: ReorderOrderTrackingsDto): Boolean {
        val uniqueFromPointNumbers = mutableSetOf<Int>()
        val uniqueToPointNumbers = mutableSetOf<Int>()

        requestDto.fromPointNumberToPointNumber.forEach {
            val fromAdded = uniqueToPointNumbers.add(it.value)
            if (!fromAdded) {
                return false
            }
            val toAdded = uniqueFromPointNumbers.add(it.key)
            if (!toAdded) {
                return false
            }
        }

        uniqueToPointNumbers.forEach {
            if (!uniqueFromPointNumbers.contains(it)) {
                return false
            }
        }
        return true
    }

    @Transactional
    override suspend fun update(requestDto: UpdateOrderTrackingDto): OrderTracking? =
        withContext(Dispatchers.IO) {
            orderTrackingCoroutineRepository.update(
                requestDto.orderId,
                requestDto.pointNumber,
                requestDto.fromFacilityId,
                requestDto.destinationId,
                requestDto.destinationType,
                requestDto.carrierId,
                requestDto.status,
                requestDto.deliveryCost,
                requestDto.currency,
                requestDto.currencyDecimalMultiplier,
                requestDto.massControlValue,
                requestDto.massMeasure,
                requestDto.parsedEstimatedDeliveryAt,
                requestDto.parsedDeliveredAt
            )
        }

    @Transactional
    override suspend fun deleteAllByOrderId(orderId: String): List<OrderTracking> =
        withContext(Dispatchers.IO) {
            orderTrackingCoroutineRepository.deleteAllByOrderId(orderId)
        }

    @Transactional
    override suspend fun deleteByExternalId(orderId: String, pointNumber: Int): OrderTracking? =
        withContext(Dispatchers.IO) {
            val deletedOrderTracking = orderTrackingCoroutineRepository.deleteByExternalId(orderId, pointNumber)
                ?: return@withContext null

            var followingPointNumber = pointNumber + 1

            // Shift all following order tracking's point numbers by one
            do {
                val shiftedOrderTracking =
                    orderTrackingCoroutineRepository.setPointNumber(
                        orderId, followingPointNumber, followingPointNumber - 1
                    )
                followingPointNumber++
            } while (shiftedOrderTracking != null)

            deletedOrderTracking
        }

    @Transactional
    override suspend fun deleteAll(): Unit =
        withContext(Dispatchers.IO) {
            orderTrackingCoroutineRepository.deleteAll()
        }

    @Transactional
    override suspend fun populateGeneratedTestData(
        ordersCount: Int,
        pointsCount: Int,
        addMassControlErrorsCount: Int
    ): List<OrderTracking> =
        withContext(Dispatchers.IO) {

            val orderTrackings = mutableListOf<OrderTracking>()

            (0..ordersCount).map { orderIdNumber ->

                // Pick starting point facility
                var fromFacilityId = faker.number().numberBetween(1, 1000).toString()

                // Fix currency for this orderId
                val currency = faker.currency().code()
                val currencyMultiplier = 10.toFloat().pow(faker.number().numberBetween(1, 3)).toInt()

                // Pick mass control option
                val hasMassControl =
                    if (addMassControlErrorsCount == 0)
                        faker.options().option(true, false)
                    else
                        true

                val trueMassControlValue = if (hasMassControl) faker.number().numberBetween(1, 1000) else null
                val massMeasure = if (hasMassControl) faker.options().option( "kg", "g", "t") else null

                // Fill massControlValues list with true values
                val massControlValues: MutableList<Int?> = (0..pointsCount).map { _ ->
                    trueMassControlValue
                }.toMutableList()

                // Fill massControlValues list with erronous values
                (0..addMassControlErrorsCount).map { _ ->
                    massControlValues[faker.number().numberBetween(1, pointsCount)] = faker.number().numberBetween(1, 1000)
                }

                (0..pointsCount).map { pointNumber ->

                    // Consider last point is not delivered yet
                    val isDelivered = pointNumber != pointsCount

                    val savedOrderTracking = orderTrackingCoroutineRepository.save(
                        generateOrderTracking(
                            faker,
                            isDelivered,
                            hasMassControl,
                            massControlValues[pointNumber],
                            massMeasure,
                            orderIdNumber.toString(),
                            pointNumber,
                            fromFacilityId,
                            null,
                            currency,
                            currencyMultiplier
                        )
                    )

                    fromFacilityId = savedOrderTracking.destinationId
                    orderTrackings.add(savedOrderTracking)
                }
            }

            orderTrackings
        }
}
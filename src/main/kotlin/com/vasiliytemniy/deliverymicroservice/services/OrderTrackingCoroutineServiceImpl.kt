package com.vasiliytemniy.deliverymicroservice.services

import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import com.vasiliytemniy.deliverymicroservice.dto.*
import com.vasiliytemniy.deliverymicroservice.exceptions.OrderTrackingNotFoundException
import com.vasiliytemniy.deliverymicroservice.repositories.OrderTrackingCoroutineRepository
import jakarta.validation.Valid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderTrackingCoroutineServiceImpl(
    private val orderTrackingCoroutineRepository: OrderTrackingCoroutineRepository
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
}
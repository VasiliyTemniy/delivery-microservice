package com.vasiliytemniy.deliverymicroservice.services

import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import com.vasiliytemniy.deliverymicroservice.dto.*
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
    override fun getFlowByOrderId(orderId: Long): Flow<OrderTracking> =
        orderTrackingCoroutineRepository.findAllByOrderId(orderId)

    @Transactional(readOnly = true)
    override suspend fun getPageByOrderId(requestDto: GetOrderTrackingsByOrderIdDto): Page<OrderTracking> =
        withContext(Dispatchers.IO) {
            orderTrackingCoroutineRepository.findPageByOrderId(requestDto.orderId, requestDto.pageable)
        }

    @Transactional(readOnly = true)
    override suspend fun getLastByOrderId(orderId: Long): OrderTracking? =
        withContext(Dispatchers.IO) {
            orderTrackingCoroutineRepository.findLastByOrderId(orderId)
        }

    @Transactional(readOnly = true)
    override fun getFlowByCarrierId(carrierId: Long, filterActive: Boolean): Flow<OrderTracking> =
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
    override suspend fun reorder(requestDto: ReorderOrderTrackingsDto): List<OrderTracking> {
        TODO("Not yet implemented")
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
    override suspend fun deleteAllByOrderId(orderId: Long): List<OrderTracking> =
        withContext(Dispatchers.IO) {
            orderTrackingCoroutineRepository.deleteAllByOrderId(orderId)
        }

    @Transactional
    override suspend fun deleteByExternalId(orderId: Long, pointNumber: Int) =
        withContext(Dispatchers.IO) {
            // TODO!: Reorder after deleting
            orderTrackingCoroutineRepository.deleteByExternalId(orderId, pointNumber)
        }
}
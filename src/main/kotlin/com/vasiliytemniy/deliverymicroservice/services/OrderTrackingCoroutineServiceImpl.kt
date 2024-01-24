package com.vasiliytemniy.deliverymicroservice.services

import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import com.vasiliytemniy.deliverymicroservice.dto.GetOrderTrackingsByCarrierIdDto
import com.vasiliytemniy.deliverymicroservice.dto.GetOrderTrackingsByOrderIdDto
import com.vasiliytemniy.deliverymicroservice.dto.SetOrderTrackingStatusesDto
import com.vasiliytemniy.deliverymicroservice.dto.UpdateOrderTrackingDto
import com.vasiliytemniy.deliverymicroservice.repositories.OrderTrackingCoroutineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.validation.Valid

@Service
class OrderTrackingCoroutineServiceImpl(
    private val orderTrackingCoroutineRepository: OrderTrackingCoroutineRepository
) : OrderTrackingCoroutineService {

    @Transactional
    override suspend fun createOrderTracking(@Valid orderTracking: OrderTracking): OrderTracking =
        withContext(Dispatchers.IO) {
            orderTrackingCoroutineRepository.save(orderTracking)
        }

    @Transactional(readOnly = true)
    override suspend fun getLastOrderTrackingByOrderId(orderId: Long): OrderTracking? =
        withContext(Dispatchers.IO) {
            orderTrackingCoroutineRepository.findLastByOrderId(orderId)
        }

    @Transactional(readOnly = true)
    override fun getOrderTrackingsByOrderIdFlow(requestDto: GetOrderTrackingsByOrderIdDto): Flow<OrderTracking> =
        orderTrackingCoroutineRepository.findPageByOrderId(requestDto.orderId, requestDto.pageable)

    @Transactional(readOnly = true)
    override fun getOrderTrackingsByCarrierIdFlow(requestDto: GetOrderTrackingsByCarrierIdDto, active: Boolean): Flow<OrderTracking> =
        if (active)
            orderTrackingCoroutineRepository.findPageActiveByCarrierId(requestDto.carrierId, requestDto.pageable)
        else
            orderTrackingCoroutineRepository.findPageByCarrierId(requestDto.carrierId, requestDto.pageable)

    // TODO: consider implementing with single SQL query
    @Transactional
    override suspend fun setOrderTrackingStatuses(requestDto: SetOrderTrackingStatusesDto): List<OrderTracking> =
        withContext(Dispatchers.IO) {
            requestDto.orderTrackingIdentifiers
                .map {
                    orderTrackingCoroutineRepository.setOrderTrackingStatus(
                        it.orderId,
                        it.pointNumber,
                        requestDto.status,
                        requestDto.deliveredAt
                    )
                }
        }

    @Transactional
    override suspend fun updateOrderTracking(requestDto: UpdateOrderTrackingDto): OrderTracking =
        withContext(Dispatchers.IO) {
            orderTrackingCoroutineRepository.updateOrderTracking(
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
                requestDto.estimatedDeliveryAt,
                requestDto.deliveredAt
            )
        }
}
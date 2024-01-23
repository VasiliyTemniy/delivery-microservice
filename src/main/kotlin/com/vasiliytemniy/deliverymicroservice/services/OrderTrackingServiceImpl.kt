package com.vasiliytemniy.deliverymicroservice.services

import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import com.vasiliytemniy.deliverymicroservice.dto.GetOrderTrackingsByCarrierIdDto
import com.vasiliytemniy.deliverymicroservice.dto.GetOrderTrackingsByOrderIdDto
import com.vasiliytemniy.deliverymicroservice.dto.SetOrderTrackingStatusDto
import com.vasiliytemniy.deliverymicroservice.repositories.OrderTrackingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.validation.Valid

@Service
class OrderTrackingServiceImpl(
    private val orderTrackingRepository: OrderTrackingRepository
) : OrderTrackingService {

    @Transactional
    override suspend fun createOrderTracking(@Valid orderTracking: OrderTracking): OrderTracking =
        withContext(Dispatchers.IO) {
            orderTrackingRepository.save(orderTracking)
        }

    @Transactional(readOnly = true)
    override suspend fun getOrderTrackingsByOrderId(requestDto: GetOrderTrackingsByOrderIdDto): Page<OrderTracking> =
        withContext(Dispatchers.IO) {
            orderTrackingRepository.findByOrderId(requestDto.orderId, requestDto.pageable)
        }

    @Transactional(readOnly = true)
    override suspend fun getOrderTrackingsByCarrierId(requestDto: GetOrderTrackingsByCarrierIdDto): Page<OrderTracking> =
        withContext(Dispatchers.IO) {
            orderTrackingRepository.findByCarrierId(requestDto.carrierId, requestDto.pageable)
        }

    @Transactional(readOnly = true)
    override fun getOrderTrackingsByOrderIdFlow(requestDto: GetOrderTrackingsByOrderIdDto): Flow<OrderTracking> =
        orderTrackingRepository.findByOrderIdFlow(requestDto.orderId, requestDto.pageable)

    @Transactional(readOnly = true)
    override fun getOrderTrackingsByCarrierIdFlow(requestDto: GetOrderTrackingsByCarrierIdDto): Flow<OrderTracking> =
        orderTrackingRepository.findByCarrierIdFlow(requestDto.carrierId, requestDto.pageable)

    @Transactional(readOnly = true)
    override suspend fun getLastOrderTrackingByOrderId(orderId: Long): OrderTracking? =
        withContext(Dispatchers.IO) {
            orderTrackingRepository.findLastByOrderId(orderId)
        }

    @Transactional(readOnly = true)
    override suspend fun getActiveOrderTrackingsByCarrierId(requestDto: GetOrderTrackingsByCarrierIdDto): Page<OrderTracking> =
        withContext(Dispatchers.IO) {
            orderTrackingRepository.findActiveByCarrierId(requestDto.carrierId, requestDto.pageable)
        }

    @Transactional
    override suspend fun setOrderTrackingStatus(requestDto: SetOrderTrackingStatusDto): OrderTracking =
        withContext(Dispatchers.IO) {
            orderTrackingRepository.setOrderTrackingStatus(requestDto.orderId, requestDto.pointNumber, requestDto.status, requestDto.deliveredAt)
        }

    companion object {
        private const val CREATE_ORDER_TRACKING = "OrderTrackingService.createOrderTracking"
    }
}
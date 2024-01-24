package com.vasiliytemniy.deliverymicroservice.services

import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import com.vasiliytemniy.deliverymicroservice.dto.GetOrderTrackingsByOrderIdDto
import com.vasiliytemniy.deliverymicroservice.dto.GetOrderTrackingsByCarrierIdDto
import com.vasiliytemniy.deliverymicroservice.dto.SetOrderTrackingStatusesDto
import com.vasiliytemniy.deliverymicroservice.dto.UpdateOrderTrackingDto
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service


@Service
interface OrderTrackingCoroutineService {

    suspend fun createOrderTracking(orderTracking: OrderTracking): OrderTracking

    fun getOrderTrackingsByOrderIdFlow(requestDto: GetOrderTrackingsByOrderIdDto): Flow<OrderTracking>

    suspend fun getLastOrderTrackingByOrderId(orderId: Long): OrderTracking?

    fun getOrderTrackingsByCarrierIdFlow(requestDto: GetOrderTrackingsByCarrierIdDto, active: Boolean): Flow<OrderTracking>

    suspend fun setOrderTrackingStatuses(requestDto: SetOrderTrackingStatusesDto): List<OrderTracking>

    suspend fun updateOrderTracking(requestDto: UpdateOrderTrackingDto): OrderTracking?

}
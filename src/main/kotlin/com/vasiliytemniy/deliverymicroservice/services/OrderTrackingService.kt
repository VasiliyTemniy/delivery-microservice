package com.vasiliytemniy.deliverymicroservice.services

import org.springframework.data.domain.Page
import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import com.vasiliytemniy.deliverymicroservice.dto.GetOrderTrackingsByOrderIdDto
import com.vasiliytemniy.deliverymicroservice.dto.GetOrderTrackingsByCarrierIdDto
import com.vasiliytemniy.deliverymicroservice.dto.SetOrderTrackingStatusDto
import org.springframework.stereotype.Service


@Service
interface OrderTrackingService {

    suspend fun createOrderTracking(orderTracking: OrderTracking): OrderTracking

//    suspend fun getOrderTrackingsByOrderId(requestDto: GetOrderTrackingsByOrderIdDto): Page<OrderTracking>
//
//    suspend fun getOrderTrackingsByCarrierId(requestDto: GetOrderTrackingsByCarrierIdDto): Page<OrderTracking>
//
//    suspend fun updateOrderTracking(orderTracking: OrderTracking): OrderTracking
//
//    suspend fun setOrderTrackingStatus(requestDto: SetOrderTrackingStatusDto): OrderTracking

}
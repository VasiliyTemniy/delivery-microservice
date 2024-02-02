package com.vasiliytemniy.deliverymicroservice.services

import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import com.vasiliytemniy.deliverymicroservice.dto.*
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service


@Service
interface OrderTrackingCoroutineService {

    suspend fun create(orderTracking: OrderTracking): OrderTracking

    fun getFlowByOrderId(orderId: String): Flow<OrderTracking>

    suspend fun getPageByOrderId(requestDto: GetOrderTrackingsByOrderIdDto): Page<OrderTracking>

    suspend fun getLastByOrderId(orderId: String): OrderTracking?

    fun getFlowByCarrierId(carrierId: String, filterActive: Boolean): Flow<OrderTracking>

    suspend fun getPageByCarrierId(requestDto: GetOrderTrackingsByCarrierIdDto): Page<OrderTracking>

    suspend fun getPageByFilters(requestDto: GetOrderTrackingsByFiltersDto): Page<OrderTracking>

    suspend fun setStatuses(requestDto: SetOrderTrackingStatusesDto): List<OrderTracking>

    suspend fun reorder(requestDto: ReorderOrderTrackingsDto, performRequestCheck: Boolean = true): List<OrderTracking>

    suspend fun update(requestDto: UpdateOrderTrackingDto): OrderTracking?

    suspend fun deleteAllByOrderId(orderId: String): List<OrderTracking>

    suspend fun deleteByExternalId(orderId: String, pointNumber: Int): OrderTracking?

    suspend fun deleteAll(): Unit

    suspend fun populateGeneratedTestData(
        ordersCount: Int = 10,
        pointsCount: Int = 10,
        addMassControlErrorsCount: Int = 0
    ): List<OrderTracking>

}
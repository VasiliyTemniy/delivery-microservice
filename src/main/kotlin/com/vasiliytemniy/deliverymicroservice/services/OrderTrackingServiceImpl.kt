package com.vasiliytemniy.deliverymicroservice.services

import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import com.vasiliytemniy.deliverymicroservice.repositories.OrderTrackingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    companion object {
        private const val CREATE_ORDER_TRACKING = "OrderTrackingService.createOrderTracking"
    }
}
package com.vasiliytemniy.deliverymicroservice.repositories

import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import com.vasiliytemniy.deliverymicroservice.dto.IdFilterGroup
import com.vasiliytemniy.deliverymicroservice.dto.TimeFilterGroup
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository


@Repository
interface OrderTrackingCoroutineCustomRepository {

    suspend fun findPageByOrderId(orderId: String, pageable: Pageable): Page<OrderTracking>

    suspend fun findPageByCarrierId(carrierId: String, pageable: Pageable, filterActive: Boolean): Page<OrderTracking>

    suspend fun findPageByFilters(
        idFilters: List<IdFilterGroup>,
        timeFilters: List<TimeFilterGroup>,
        eitherEqualStatusFilters: List<String>,
        neitherEqualStatusFilters: List<String>,
        hasMassMeasureFilter: Boolean,
        pageable: Pageable
    ): Page<OrderTracking>

    fun findAllByCarrierId(carrierId: String, filterActive: Boolean): Flow<OrderTracking>

    suspend fun setPointNumber(orderId: String, fromPointNumber: Int, toPointNumber: Int): OrderTracking?

}
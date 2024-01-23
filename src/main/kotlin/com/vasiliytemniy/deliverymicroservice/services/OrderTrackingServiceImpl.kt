package com.vasiliytemniy.deliverymicroservice.services

import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import com.vasiliytemniy.deliverymicroservice.dto.GetOrderTrackingsByCarrierIdDto
import com.vasiliytemniy.deliverymicroservice.dto.GetOrderTrackingsByOrderIdDto
import com.vasiliytemniy.deliverymicroservice.dto.SetOrderTrackingStatusDto
import com.vasiliytemniy.deliverymicroservice.repositories.OrderTrackingCoroutineRepository
import com.vasiliytemniy.deliverymicroservice.repositories.OrderTrackingReactiveRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.validation.Valid

@Service
class OrderTrackingServiceImpl(
    private val orderTrackingCoroutineRepository: OrderTrackingCoroutineRepository,
    private val orderTrackingReactiveRepository: OrderTrackingReactiveRepository
) : OrderTrackingService {

    @Transactional
    override suspend fun createOrderTracking(@Valid orderTracking: OrderTracking): OrderTracking =
        withContext(Dispatchers.IO) {
            orderTrackingCoroutineRepository.save(orderTracking)
        }

    @Transactional(readOnly = true)
    override suspend fun getOrderTrackingsByOrderId(requestDto: GetOrderTrackingsByOrderIdDto): Mono<Page<OrderTracking>> =
        orderTrackingReactiveRepository.findByOrderId(requestDto.orderId, requestDto.pageable)
            .collectList()
            .zipWith(this.orderTrackingReactiveRepository.count())
            .map { PageImpl(it.t1, requestDto.pageable, it.t2) }

    @Transactional(readOnly = true)
    override suspend fun getOrderTrackingsByCarrierId(requestDto: GetOrderTrackingsByCarrierIdDto): Mono<Page<OrderTracking>> =
        orderTrackingReactiveRepository.findByCarrierId(requestDto.carrierId, requestDto.pageable)
            .collectList()
            .zipWith(this.orderTrackingReactiveRepository.count())
            .map { PageImpl(it.t1, requestDto.pageable, it.t2) }

    @Transactional(readOnly = true)
    override fun getOrderTrackingsByOrderIdFlow(requestDto: GetOrderTrackingsByOrderIdDto): Flow<OrderTracking> =
        orderTrackingCoroutineRepository.findByOrderIdFlow(requestDto.orderId, requestDto.pageable)

    @Transactional(readOnly = true)
    override fun getOrderTrackingsByCarrierIdFlow(requestDto: GetOrderTrackingsByCarrierIdDto): Flow<OrderTracking> =
        orderTrackingCoroutineRepository.findByCarrierIdFlow(requestDto.carrierId, requestDto.pageable)

    @Transactional(readOnly = true)
    override suspend fun getLastOrderTrackingByOrderId(orderId: Long): Mono<OrderTracking>? =
        withContext(Dispatchers.IO) {
            orderTrackingReactiveRepository.findLastByOrderId(orderId)
        }

    @Transactional(readOnly = true)
    override suspend fun getActiveOrderTrackingsByCarrierId(requestDto: GetOrderTrackingsByCarrierIdDto): Mono<Page<OrderTracking>> =
        orderTrackingReactiveRepository.findActiveByCarrierId(requestDto.carrierId, requestDto.pageable)
            .buffer(requestDto.pageable.pageSize, (requestDto.pageable.pageNumber + 1))
            .elementAt(requestDto.pageable.pageNumber,  ArrayList<OrderTracking>())
            .flatMapMany { Flux.fromIterable(it) }
            .collectList()
            .zipWith(this.orderTrackingReactiveRepository.count())
            .map { PageImpl(it.t1, requestDto.pageable, it.t2) }

    @Transactional
    override suspend fun setOrderTrackingStatus(requestDto: SetOrderTrackingStatusDto): Mono<OrderTracking> =
        withContext(Dispatchers.IO) {
            orderTrackingReactiveRepository.setOrderTrackingStatus(requestDto.orderId, requestDto.pointNumber, requestDto.status, requestDto.deliveredAt)
        }

    companion object {
        private const val CREATE_ORDER_TRACKING = "OrderTrackingService.createOrderTracking"
    }
}
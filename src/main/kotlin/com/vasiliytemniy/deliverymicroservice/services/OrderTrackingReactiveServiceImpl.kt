package com.vasiliytemniy.deliverymicroservice.services

import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import com.vasiliytemniy.deliverymicroservice.dto.GetOrderTrackingsByCarrierIdDto
import com.vasiliytemniy.deliverymicroservice.dto.GetOrderTrackingsByOrderIdDto
import com.vasiliytemniy.deliverymicroservice.dto.SetOrderTrackingStatusesDto
import com.vasiliytemniy.deliverymicroservice.dto.UpdateOrderTrackingDto
import com.vasiliytemniy.deliverymicroservice.repositories.OrderTrackingReactiveRepository
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class OrderTrackingReactiveServiceImpl(
    private val orderTrackingReactiveRepository: OrderTrackingReactiveRepository
) : OrderTrackingReactiveService {

    @Transactional
    override fun createOrderTracking(@Valid orderTracking: OrderTracking): Mono<OrderTracking> =
        orderTrackingReactiveRepository.save(orderTracking)

    @Transactional(readOnly = true)
    override fun getOrderTrackingsByOrderId(requestDto: GetOrderTrackingsByOrderIdDto): Mono<Page<OrderTracking>> =
        orderTrackingReactiveRepository.findPageByOrderId(requestDto.orderId, requestDto.pageable)
            .collectList()
            .zipWith(this.orderTrackingReactiveRepository.count())
            .map { PageImpl(it.t1, requestDto.pageable, it.t2) }

    @Transactional(readOnly = true)
    override fun getOrderTrackingsByCarrierId(requestDto: GetOrderTrackingsByCarrierIdDto, active: Boolean): Mono<Page<OrderTracking>> =
        if (active)
            orderTrackingReactiveRepository.findPageActiveByCarrierId(requestDto.carrierId, requestDto.pageable)
                .collectList()
                .zipWith(this.orderTrackingReactiveRepository.count())
                .map { PageImpl(it.t1, requestDto.pageable, it.t2) }
        else
            orderTrackingReactiveRepository.findPageByCarrierId(requestDto.carrierId, requestDto.pageable)
                .collectList()
                .zipWith(this.orderTrackingReactiveRepository.count())
                .map { PageImpl(it.t1, requestDto.pageable, it.t2) }

    @Transactional(readOnly = true)
    override fun getLastOrderTrackingByOrderId(orderId: Long): Mono<OrderTracking>? =
        orderTrackingReactiveRepository.findLastByOrderId(orderId)

    @Transactional
    override fun setOrderTrackingStatuses(requestDto: SetOrderTrackingStatusesDto): Flux<OrderTracking> =
        Flux.merge(
            requestDto.orderTrackingIdentifiers
                .map {
                    orderTrackingReactiveRepository.setOrderTrackingStatus(
                        it.orderId,
                        it.pointNumber,
                        requestDto.status,
                        requestDto.deliveredAt
                    )
                }
        )

    @Transactional
    override fun updateOrderTracking(requestDto: UpdateOrderTrackingDto): Mono<OrderTracking> =
        orderTrackingReactiveRepository.updateOrderTracking(
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

    @Transactional
    override fun deleteAllByOrderId(orderId: Long): Flux<OrderTracking> =
        orderTrackingReactiveRepository.deleteAllByOrderId(orderId)

    @Transactional
    override fun deleteByOrderTrackingIdentifier(orderId: Long, pointNumber: Int): Mono<OrderTracking> =
        orderTrackingReactiveRepository.deleteByOrderTrackingIdentifier(orderId, pointNumber)
}
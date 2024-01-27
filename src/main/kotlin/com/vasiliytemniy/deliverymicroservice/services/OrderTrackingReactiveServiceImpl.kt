package com.vasiliytemniy.deliverymicroservice.services

import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import com.vasiliytemniy.deliverymicroservice.dto.*
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
    override fun create(@Valid orderTracking: OrderTracking): Mono<OrderTracking> =
        orderTrackingReactiveRepository.save(orderTracking)

    @Transactional(readOnly = true)
    override fun getPageByOrderId(requestDto: GetOrderTrackingsByOrderIdDto): Mono<Page<OrderTracking>> =
        orderTrackingReactiveRepository.findPageByOrderId(requestDto.orderId, requestDto.pageable)
            .collectList()
            .zipWith(this.orderTrackingReactiveRepository.count())
            .map { PageImpl(it.t1, requestDto.pageable, it.t2) }

    @Transactional(readOnly = true)
    override fun getAllByOrderId(orderId: Long): Flux<OrderTracking> =
        orderTrackingReactiveRepository.findAllByOrderId(orderId)

    @Transactional(readOnly = true)
    override fun getLastByOrderId(orderId: Long): Mono<OrderTracking> =
        orderTrackingReactiveRepository.findLastByOrderId(orderId)

    @Transactional(readOnly = true)
    override fun getPageByCarrierId(requestDto: GetOrderTrackingsByCarrierIdDto): Mono<Page<OrderTracking>> =
        if (requestDto.filterActive)
            orderTrackingReactiveRepository.findPageByCarrierIdAndDeliveredAt(requestDto.carrierId, null, requestDto.pageable)
                .collectList()
                .zipWith(this.orderTrackingReactiveRepository.count())
                .map { PageImpl(it.t1, requestDto.pageable, it.t2) }
        else
            orderTrackingReactiveRepository.findPageByCarrierId(requestDto.carrierId, requestDto.pageable)
                .collectList()
                .zipWith(this.orderTrackingReactiveRepository.count())
                .map { PageImpl(it.t1, requestDto.pageable, it.t2) }

    @Transactional(readOnly = true)
    override fun getAllByCarrierId(carrierId: Long, filterActive: Boolean): Flux<OrderTracking> =
        if (filterActive)
            orderTrackingReactiveRepository.findAllByCarrierIdAndDeliveredAt(carrierId, null)
        else
            orderTrackingReactiveRepository.findAllByCarrierId(carrierId)

    @Transactional
    override fun setStatuses(requestDto: SetOrderTrackingStatusesDto): Flux<OrderTracking> =
        Flux.merge(
            requestDto.orderTrackingExternalIds
                .map {
                    orderTrackingReactiveRepository.setStatus(
                        it.orderId,
                        it.pointNumber,
                        requestDto.status,
                        requestDto.parsedDeliveredAt
                    )
                }
        )

    @Transactional
    override fun reorder(requestDto: ReorderOrderTrackingsDto): Flux<OrderTracking> {
        TODO("Not yet implemented")
    }

    @Transactional
    override fun update(requestDto: UpdateOrderTrackingDto): Mono<OrderTracking> =
        orderTrackingReactiveRepository.update(
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

    @Transactional
    override fun deleteAllByOrderId(orderId: Long): Flux<OrderTracking> =
        orderTrackingReactiveRepository.deleteAllByOrderId(orderId)

    @Transactional
    override fun deleteByExternalId(orderId: Long, pointNumber: Int): Mono<OrderTracking> =
        // TODO!: Reorder after deleting
        orderTrackingReactiveRepository.deleteByExternalId(orderId, pointNumber)
}
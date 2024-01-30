package com.vasiliytemniy.deliverymicroservice.services

import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import com.vasiliytemniy.deliverymicroservice.dto.*
import com.vasiliytemniy.deliverymicroservice.exceptions.OrderTrackingNotFoundException
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
    override fun create(@Valid orderTracking: OrderTracking): Mono<OrderTracking> {
        var savedOrderTracking = orderTrackingReactiveRepository.save(orderTracking)

        // Workaround for returned order tracking's point number - applied by db trigger after db save
        // For the case if request orderTracking doesn't have explicit point number
        if (orderTracking.pointNumber == null) {
            savedOrderTracking = orderTrackingReactiveRepository.findLastByOrderId(orderTracking.orderId)
        }

        return savedOrderTracking
    }

    @Transactional(readOnly = true)
    override fun getPageByOrderId(requestDto: GetOrderTrackingsByOrderIdDto): Mono<Page<OrderTracking>> =
        orderTrackingReactiveRepository.findPageByOrderId(requestDto.orderId, requestDto.pageable)
            .collectList()
            .zipWith(this.orderTrackingReactiveRepository.countByOrderId(requestDto.orderId))
            .map { PageImpl(it.t1, requestDto.pageable, it.t2) }

    @Transactional(readOnly = true)
    override fun getAllByOrderId(orderId: String): Flux<OrderTracking> =
        orderTrackingReactiveRepository.findAllByOrderId(orderId)

    @Transactional(readOnly = true)
    override fun getLastByOrderId(orderId: String): Mono<OrderTracking> =
        orderTrackingReactiveRepository.findLastByOrderId(orderId)

    @Transactional(readOnly = true)
    override fun getPageByCarrierId(requestDto: GetOrderTrackingsByCarrierIdDto): Mono<Page<OrderTracking>> =
        orderTrackingReactiveRepository.findPageByCarrierId(requestDto.carrierId, requestDto.pageable, requestDto.filterActive)
            .collectList()
            .zipWith(this.orderTrackingReactiveRepository.countByCarrierId(requestDto.carrierId, requestDto.filterActive))
            .map { PageImpl(it.t1, requestDto.pageable, it.t2) }

    @Transactional(readOnly = true)
    override fun getAllByCarrierId(carrierId: String, filterActive: Boolean): Flux<OrderTracking> =
        orderTrackingReactiveRepository.findAllByCarrierId(carrierId, filterActive)

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
            requestDto.lat,
            requestDto.lon,
            requestDto.parsedEstimatedDeliveryAt,
            requestDto.parsedDeliveredAt
        )

    @Transactional
    override fun deleteAllByOrderId(orderId: String): Flux<OrderTracking> =
        orderTrackingReactiveRepository.deleteAllByOrderId(orderId)

    @Transactional
    override fun deleteByExternalId(orderId: String, pointNumber: Int): Mono<OrderTracking> =
        orderTrackingReactiveRepository.deleteByExternalId(orderId, pointNumber)
            .flatMap {
                var followingPointNumber = pointNumber + 1

                // Shift all following order tracking's point numbers by one
                do {
                    val shiftedOrderTracking =
                        orderTrackingReactiveRepository.setPointNumber(
                            orderId, followingPointNumber, followingPointNumber - 1
                        )
                    followingPointNumber++
                } while (shiftedOrderTracking.block() != null)

                Mono.just(it)
            }

}
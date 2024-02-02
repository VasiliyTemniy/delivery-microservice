package com.vasiliytemniy.deliverymicroservice.services

import com.vasiliytemniy.deliverymicroservice.domain.DeliveryMeta
import com.vasiliytemniy.deliverymicroservice.dto.CalculateDeliveryMetaDto
import org.springframework.stereotype.Service


@Service
class DeliveryMetaServiceImpl(): DeliveryMetaService {

    override suspend fun calculateDeliveryMeta(requestDto: CalculateDeliveryMetaDto): DeliveryMeta {
        TODO("Not yet implemented")
    }

}
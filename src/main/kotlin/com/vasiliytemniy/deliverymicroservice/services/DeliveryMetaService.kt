package com.vasiliytemniy.deliverymicroservice.services

import com.vasiliytemniy.deliverymicroservice.domain.DeliveryMeta
import com.vasiliytemniy.deliverymicroservice.dto.CalculateDeliveryMetaDto


interface DeliveryMetaService {

    suspend fun calculateDeliveryMeta(requestDto: CalculateDeliveryMetaDto): DeliveryMeta

}
package com.vasiliytemniy.deliverymicroservice.repositories

class LongSqlQueries {

    companion object {
        const val ORDER_TRACKING_UPDATE_SQL_QUERY = """
            UPDATE order_trackings
            SET
                from_facility_id = :fromFacilityId,
                destination_id = :destinationId,
                destination_type = :destinationType,
                carrier_id = :carrierId,
                status = :status,
                delivery_cost = :deliveryCost,
                currency = :currency,
                currency_decimal_multiplier = :currencyDecimalMultiplier,
                mass_control_value = :massControlValue,
                mass_measure = :massMeasure,
                estimated_delivery_at = :estimatedDeliveryAt,
                delivered_at = :deliveredAt
            WHERE
                order_id = :orderId, point_number = :pointNumber
            RETURNING *
        """

        const val SET_ORDER_TRACKING_STATUS_SQL_QUERY = """
            UPDATE order_trackings
            SET
                status = :status,
                delivered_at = :deliveredAt
            WHERE
                order_id = :orderId, point_number = :pointNumber
            RETURNING *
        """
    }

}
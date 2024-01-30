package com.vasiliytemniy.deliverymicroservice.repositories

class SqlQueries {

    companion object {
        const val FIND_LAST_BY_ORDER_ID_SQL_QUERY = """
            SELECT * FROM delivery.order_trackings
            WHERE order_id = :orderId
            ORDER BY point_number DESC
            LIMIT 1
        """

        const val UPDATE_ORDER_TRACKING_SQL_QUERY = """
            UPDATE delivery.order_trackings
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
                lat = :lat,
                lon = :lon,
                estimated_delivery_at = :estimatedDeliveryAt,
                delivered_at = :deliveredAt
            WHERE
                order_id = :orderId AND point_number = :pointNumber
            RETURNING *
        """

        const val SET_ORDER_TRACKING_STATUS_SQL_QUERY = """
            UPDATE delivery.order_trackings
            SET
                status = :status,
                delivered_at = :deliveredAt
            WHERE
                order_id = :orderId AND point_number = :pointNumber
            RETURNING *
        """

        const val SET_POINT_NUMBER_SQL_QUERY = """
            UPDATE delivery.order_trackings
            SET
                point_number = :toPointNumber
            WHERE
                order_id = :orderId AND point_number = :fromPointNumber
            RETURNING *
        """

        const val DELETE_ALL_ORDER_TRACKINGS_BY_ORDER_ID_SQL_QUERY = """
            DELETE FROM delivery.order_trackings
            WHERE
                order_id = :orderId
            RETURNING *
        """

        const val DELETE_ORDER_TRACKING_SQL_QUERY = """
            DELETE FROM delivery.order_trackings
            WHERE
                order_id = :orderId AND point_number = :pointNumber
            RETURNING *
        """

        const val SELECT_COUNT_SQL_QUERY = """
            SELECT count(id) AS total FROM delivery.order_trackings
        """

        const val SELECT_COUNT_BY_ORDER_ID_SQL_QUERY = """
            SELECT count(id) AS total FROM delivery.order_trackings
            WHERE
                order_id = :orderId
        """

        const val SELECT_COUNT_BY_CARRIER_ID_SQL_QUERY = """
            SELECT count(id) AS total FROM delivery.order_trackings
            WHERE
                carrier_id = :carrierId
        """

        const val SELECT_COUNT_ACTIVE_BY_CARRIER_ID_SQL_QUERY = """
            SELECT count(id) AS total FROM delivery.order_trackings
            WHERE
                carrier_id = :carrierId AND delivered_at IS NULL
        """
    }

}
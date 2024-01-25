package com.vasiliytemniy.deliverymicroservice.repositories

class SqlQueries {

    companion object {
        const val FIND_PAGE_BY_ORDER_ID_SQL_QUERY = """
            SELECT * FROM order_trackings
            WHERE order_id = :orderId
            ORDER BY point_number ASC
            LIMIT :pageable.pageSize
            OFFSET :pageable.offset
        """

        const val FIND_ALL_BY_ORDER_ID_SQL_QUERY = """
            SELECT * FROM order_trackings
            WHERE order_id = :orderId
            ORDER BY point_number ASC
        """

        const val FIND_LAST_BY_ORDER_ID_SQL_QUERY = """
            SELECT * FROM order_trackings
            WHERE order_id = :orderId
            ORDER BY point_number DESC
            LIMIT 1
        """

        const val FIND_PAGE_BY_CARRIER_ID_SQL_QUERY = """
            SELECT * FROM order_trackings
            WHERE carrier_id = :carrierId
            ORDER BY point_number ASC
            LIMIT :pageable.pageSize
            OFFSET :pageable.offset
        """

        const val FIND_ALL_BY_CARRIER_ID_SQL_QUERY = """
            SELECT * FROM order_trackings
            WHERE carrier_id = :carrierId
            ORDER BY point_number ASC
        """

        const val FIND_PAGE_ACTIVE_BY_CARRIER_ID_SQL_QUERY = """
            SELECT * FROM order_trackings
            WHERE carrier_id = :carrierId AND delivered_at IS NULL
            ORDER BY point_number ASC
            LIMIT :pageable.pageSize
            OFFSET :pageable.offset
        """

        const val UPDATE_ORDER_TRACKING_SQL_QUERY = """
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

        const val DELETE_ALL_ORDER_TRACKINGS_BY_ORDER_ID_SQL_QUERY = """
            DELETE FROM order_trackings
            WHERE
                order_id = :orderId
            RETURNING *
        """

        const val DELETE_ORDER_TRACKING_SQL_QUERY = """
            DELETE FROM order_trackings
            WHERE
                order_id = :orderId, point_number = :pointNumber
            RETURNING *
        """
    }

}
package com.vasiliytemniy.deliverymicroservice.domain

enum class IdFilterType(val value: String) {
    ORDER_ID("order_id"),
    CARRIER_ID("carrier_id"),
    FROM_FACILITY_ID("from_facility_id"),
    DESTINATION_ID("destination_id");

    override fun toString(): String {
        return super.toString().lowercase()
    }

    companion object {
        fun fromValue(value: String): IdFilterType {
            return when (value.lowercase()) {
                "order_id" -> ORDER_ID
                "carrier_id" -> CARRIER_ID
                "from_facility_id" -> FROM_FACILITY_ID
                "destination_id" -> DESTINATION_ID
                else -> throw IllegalArgumentException()
            }
        }
    }
}
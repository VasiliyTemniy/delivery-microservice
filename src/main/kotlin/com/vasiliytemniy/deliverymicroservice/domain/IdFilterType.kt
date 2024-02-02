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
        fun isIdFilterType(value: String): Boolean {
            return when (value) {
                "order_id" -> true
                "carrier_id" -> true
                "from_facility_id" -> true
                "destination_id" -> true
                else -> false
            }
        }
    }
}
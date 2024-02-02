package com.vasiliytemniy.deliverymicroservice.domain


enum class NullablesFilterType(val value: String) {
    MASS_CONTROL_VALUE("mass_control_value"),
    MASS_MEASURE("mass_measure"),
    LAT("lat"),
    LON("lon"),
    ESTIMATED_DELIVERY_AT("estimated_delivery_at"),
    DELIVERED_AT("delivered_at");

    override fun toString(): String {
        return super.toString().lowercase()
    }

    companion object {
        fun isNullablesFilterType(value: String): Boolean {
            return when (value) {
                "mass_control_value" -> true
                "mass_measure" -> true
                "lat" -> true
                "lon" -> true
                "estimated_delivery_at" -> true
                "delivered_at" -> true
                else -> false
            }
        }
    }
}
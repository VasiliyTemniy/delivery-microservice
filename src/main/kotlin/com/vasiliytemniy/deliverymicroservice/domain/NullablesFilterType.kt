package com.vasiliytemniy.deliverymicroservice.domain


enum class NullablesFilterType(val value: String) {
    MASS_CONTROL_VALUE("mass_control_value"),
    MASS_MEASURE("mass_measure"),
    ESTIMATED_DELIVERY_AT("estimated_delivery_at"),
    DELIVERED_AT("delivered_at");

    override fun toString(): String {
        return super.toString().lowercase()
    }

    companion object {
        fun fromValue(value: String): NullablesFilterType {
            return when (value.lowercase()) {
                "mass_control_value" -> MASS_CONTROL_VALUE
                "mass_measure" -> MASS_MEASURE
                "estimated_delivery_at" -> ESTIMATED_DELIVERY_AT
                "delivered_at" -> DELIVERED_AT
                else -> throw IllegalArgumentException()
            }
        }
    }
}
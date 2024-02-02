package com.vasiliytemniy.deliverymicroservice.domain


enum class DeliveryCostCalculationType(val value: String) {
    FIXED("fixed"),
    DISTANCE("distance"),
    MASS("mass"),
    VOLUME("volume");

    override fun toString(): String {
        return super.toString().lowercase()
    }

    companion object {
        fun fromValue(value: String): DeliveryCostCalculationType {
            return when (value.lowercase()) {
                "fixed" -> FIXED
                "distance" -> DISTANCE
                "mass" -> MASS
                "volume" -> VOLUME
                else -> throw IllegalArgumentException()
            }
        }
    }
}
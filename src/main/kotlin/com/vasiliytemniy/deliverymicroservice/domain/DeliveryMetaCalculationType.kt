package com.vasiliytemniy.deliverymicroservice.domain


enum class DeliveryMetaCalculationType(val value: String) {
    ONLY_COST("only_cost"),
    ONLY_ESTIMATED_TIME("only_estimated_time"),
    COST_AND_ESTIMATED_TIME("cost_and_estimated_time");

    override fun toString(): String {
        return super.toString().lowercase()
    }

    companion object {
        fun fromValue(value: String): DeliveryMetaCalculationType {
            return when (value.lowercase()) {
                "only_cost" -> ONLY_COST
                "onlycost" -> ONLY_COST
                "only_estimated_time" -> ONLY_ESTIMATED_TIME
                "onlyestimatedtime" -> ONLY_ESTIMATED_TIME
                "cost_and_estimated_time" -> COST_AND_ESTIMATED_TIME
                "costandestimatedtime" -> COST_AND_ESTIMATED_TIME
                else -> throw IllegalArgumentException()
            }
        }
    }
}
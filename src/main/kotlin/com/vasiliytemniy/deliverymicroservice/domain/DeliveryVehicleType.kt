package com.vasiliytemniy.deliverymicroservice.domain


enum class DeliveryVehicleType(val value: String) {
    CAR("car"),
    TRUCK("truck"),
    PLANE("plane"),
    TRAIN("train"),
    SHIP("ship"),
    BICYCLE("bicycle"),
    SCOOTER("scooter");

    override fun toString(): String {
        return super.toString().lowercase()
    }

    companion object {
        fun fromValue(value: String): DeliveryVehicleType {
            return when (value.lowercase()) {
                "car" -> CAR
                "truck" -> TRUCK
                "plane" -> PLANE
                "train" -> TRAIN
                "ship" -> SHIP
                "bicycle" -> BICYCLE
                "scooter" -> SCOOTER
                else -> throw IllegalArgumentException()
            }
        }
    }
}
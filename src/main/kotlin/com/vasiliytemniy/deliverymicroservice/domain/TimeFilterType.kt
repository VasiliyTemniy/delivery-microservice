package com.vasiliytemniy.deliverymicroservice.domain

enum class TimeFilterType(val value: String) {
    ESTIMATED_DELIVERY_AT("estimated_delivery_at"),
    DELIVERED_AT("delivered_at"),
    CREATED_AT("created_at"),
    UPDATED_AT("updated_at");

    override fun toString(): String {
        return super.toString().lowercase()
    }
}
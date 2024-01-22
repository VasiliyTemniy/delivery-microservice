package com.vasiliytemniy.deliverymicroservice.dto

class CalculateDeliveryCostDto(
    val calculationType: String,
    val fixedCostAddon: Int,
    val distanceStepCost: Int,
    val distanceStepQuantity: Int,
    val distanceStepMeasure: String,
    val massStepCost: Int,
    val massStepQuantity: Int,
    val massStepMeasure: String,
    val volumeStepCost: Int,
    val volumeStepQuantity: Int,
    val volumeStepMeasure: String,
    val fromAddress: String,
    val toAddress: String,
    val mass: Int,
    val volume: Int
)
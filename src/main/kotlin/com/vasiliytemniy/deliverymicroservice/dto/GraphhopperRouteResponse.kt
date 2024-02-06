package com.vasiliytemniy.deliverymicroservice.dto

import com.fasterxml.jackson.annotation.JsonProperty


data class RouteMeta(
    val distance: Double,
    val time: Long
)


data class GraphhopperRouteResponse(
    val paths: List<GraphhopperRouteResponsePath>,
    val info: GraphhopperRouteResponseInfo
)

// Only distance is needed actually, so other fields are omitted
data class GraphhopperRouteResponsePath(
    val distance: Double,
    val time: Long,
    val ascend: Double,
    val descend: Double,
//    val points: String, // actual format depends on points_encoded - manual deserialize needed
//    @JsonProperty("snapped_waypoints")
//    val snappedWaypoints: String, // actual format depends on points_encoded - manual deserialize needed
//    @JsonProperty("points_encoded")
//    val pointsEncoded: Boolean,
//    val bbox: List<Double>,
//    val instructions: List<GraphhopperRouteInstruction>,
//    val details: Any?,
//    @JsonProperty("points_order")
//    val pointsOrder: List<Int>?,
)

data class GraphhopperRouteInstruction(
    val text: String,
    @JsonProperty("street_name")
    val streetName: String?,
    val distance: Double,
    val time: Int,
    @JsonProperty("interval")
    val interval: List<Int>,
    val sign: Int?,
    @JsonProperty("exit_number")
    val exitNumber: Int?,
    @JsonProperty("turn_angle")
    val turnAngle: Double
)

data class GraphhopperRouteResponseInfo(
    val copyrights: List<String>,
    val took: Int
)
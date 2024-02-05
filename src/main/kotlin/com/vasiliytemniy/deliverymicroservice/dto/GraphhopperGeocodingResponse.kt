package com.vasiliytemniy.deliverymicroservice.dto


data class GraphhopperGeocodingResponse (
    val hits: List<GraphhopperGeocodingResponseHit>,
    val took: Int
)

data class GraphhopperGeocodingResponseHit (
    val point: GeocodingPoint,
    val osmId: Int,
    val osmType: String,
    val osmKey: String,
    val name: String,
    val country: String,
    val city: String,
    val state: String,
    val street: String,
    val housenumber: String,
    val postcode: String
)

data class GeocodingPoint (
    val lat: Double,
    val lng: Double
)
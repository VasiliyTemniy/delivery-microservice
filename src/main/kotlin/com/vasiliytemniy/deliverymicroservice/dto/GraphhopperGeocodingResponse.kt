package com.vasiliytemniy.deliverymicroservice.dto

import com.fasterxml.jackson.annotation.JsonProperty


data class GraphhopperGeocodingResponse (
    val hits: List<GraphhopperGeocodingResponseHit>,
    val took: Int
)

data class GraphhopperGeocodingResponseHit (
    val point: GeocodingPoint,
//    @JsonProperty("osm_id")
//    val osmId: String,
//    @JsonProperty("osm_type")
//    val osmType: String,
//    @JsonProperty("osm_key")
//    val osmKey: String,
    val name: String?,
    val country: String?,
    val city: String?,
    val state: String?,
    val street: String?,
    val housenumber: String?,
    val postcode: String?
)

data class GeocodingPoint (
    val lat: Double,
    val lng: Double
)
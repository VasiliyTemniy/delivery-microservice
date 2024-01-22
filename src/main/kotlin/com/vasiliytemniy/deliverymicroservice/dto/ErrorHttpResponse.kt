package com.vasiliytemniy.deliverymicroservice.dto

data class ErrorHttpResponse(
    val status: Int,
    val message: String,
    val timestamp: String
)
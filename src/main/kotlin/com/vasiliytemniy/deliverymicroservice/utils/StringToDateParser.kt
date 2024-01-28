package com.vasiliytemniy.deliverymicroservice.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


fun parseOptionalDate(request: String?): LocalDateTime? {
    return if (request == null || request == "" || request == "null") {
        null
    } else {
        LocalDateTime.parse(request, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }
}
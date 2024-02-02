package com.vasiliytemniy.deliverymicroservice.dto

import com.vasiliytemniy.deliverymicroservice.domain.IdFilterType
import com.vasiliytemniy.deliverymicroservice.domain.NullablesFilterType
import com.vasiliytemniy.deliverymicroservice.domain.TimeFilterType
import com.vasiliytemniy.deliverymicroservice.utils.parseOptionalDate
import com.vasiliytemniy.grpc.ordertracking.service.OrderTracking
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

data class GetOrderTrackingsByFiltersDto(
    val idFilters: List<IdFilterGroup>,
    val timeFilters: List<TimeFilterGroup>,
    val eitherEqualStatusFilters: List<String>,
    val neitherEqualStatusFilters: List<String>,
    val nullablesFilters: List<NullablesFilterGroup>,
    val hasMassMeasureFilter: Boolean = false,
    val pageable: Pageable
) {
    companion object
}

data class IdFilterGroup(
    val type: IdFilterType,
    val id: String
)

data class TimeFilterGroup(
    val type: TimeFilterType,
    val from: LocalDateTime?,
    val to: LocalDateTime?
)

data class NullablesFilterGroup(
    val type: NullablesFilterType,
    val isOrNotNull: Boolean
)

fun GetOrderTrackingsByFiltersDto.Companion.of(request: OrderTracking.GetPageByFiltersRequest): GetOrderTrackingsByFiltersDto =
    GetOrderTrackingsByFiltersDto(
        request.idFilterGroupsList.toList()
            .map { IdFilterGroup(IdFilterType.valueOf(it.type), it.id) },
        request.timeFilterGroupsList.toList()
            .map { TimeFilterGroup(TimeFilterType.valueOf(it.type), parseOptionalDate(it.from), parseOptionalDate(it.to)) },
        request.eitherEqualStatusFiltersList.toList(),
        request.neitherEqualStatusFiltersList.toList(),
        request.nullablesFilterGroupsList.toList()
            .map { NullablesFilterGroup(NullablesFilterType.valueOf(it.type), it.isOrNotNull) },
        request.hasMassMeasureFilter,
        PageRequest.of(request.page, request.size)
    )


fun GetOrderTrackingsByFiltersDto.Companion.of(request: Any, page: Int, size: Int): GetOrderTrackingsByFiltersDto {

    val invalidRequestPrefix = "Invalid request: getPageOrderTrackingsByFilters:"

    if (request !is LinkedHashMap<*, *>)
        throw IllegalArgumentException("$invalidRequestPrefix invalid request body$")

    if (
        request.keys.size > 6
        || request.keys.size == 0
    )
        throw IllegalArgumentException("$invalidRequestPrefix invalid request body")

    val parsedIdFilters = mutableListOf<IdFilterGroup>()

    if (request.contains("idFilters")) {
        if (request["idFilters"] !is List<*>) {
            throw IllegalArgumentException("$invalidRequestPrefix invalid idFilters")
        }

        val requestIdFilters = request["idFilters"] as List<*>
        requestIdFilters.forEach {
            if (
                it !is LinkedHashMap<*, *>
                || !it.keys.containsAll(listOf("type", "id"))
                || it["type"] !is String
                || it["id"] !is String
                || !IdFilterType.isIdFilterType(it["type"] as String)
            )
                throw IllegalArgumentException("$invalidRequestPrefix invalid idFilters")

            parsedIdFilters.add(IdFilterGroup(
                IdFilterType.valueOf(it["type"] as String),
                it["id"] as String)
            )
        }
    }

    val parsedTimeFilters = mutableListOf<TimeFilterGroup>()

    if (request.contains("timeFilters")) {
        if (request["timeFilters"] !is List<*>) {
            throw IllegalArgumentException("$invalidRequestPrefix invalid timeFilters")
        }

        val requestTimeFilters = request["timeFilters"] as List<*>
        requestTimeFilters.forEach {
            if (
                it !is LinkedHashMap<*, *>
                || !it.keys.containsAll(listOf("type", "from", "to"))
                || it["type"] !is String
                || !(it["from"] is String || it["from"] == null)
                || !(it["to"] is String || it["to"] == null)
                || !TimeFilterType.isTimeFilterType(it["type"] as String)
            )
                throw IllegalArgumentException("$invalidRequestPrefix invalid timeFilters")

            parsedTimeFilters.add(TimeFilterGroup(
                TimeFilterType.valueOf(it["type"] as String),
                parseOptionalDate(it["from"] as String?),
                parseOptionalDate(it["to"] as String?))
            )
        }
    }

    val parsedEitherEqualStatusFilters = mutableListOf<String>()

    if (request.contains("eitherEqualStatusFilters")) {
        if (request["eitherEqualStatusFilters"] !is List<*>) {
            throw IllegalArgumentException("$invalidRequestPrefix invalid eitherEqualStatusFilters")
        }

        val requestEitherEqualStatusFilters = request["eitherEqualStatusFilters"] as List<*>
        requestEitherEqualStatusFilters.forEach {
            if (
                it !is String
            )
                throw IllegalArgumentException("$invalidRequestPrefix invalid eitherEqualStatusFilters")

            parsedEitherEqualStatusFilters.add(it)
        }
    }

    val parsedNeitherEqualStatusFilters = mutableListOf<String>()

    if (request.contains("neitherEqualStatusFilters")) {
        if (request["neitherEqualStatusFilters"] !is List<*>) {
            throw IllegalArgumentException("$invalidRequestPrefix invalid neitherEqualStatusFilters")
        }

        val requestNeitherEqualStatusFilters = request["neitherEqualStatusFilters"] as List<*>
        requestNeitherEqualStatusFilters.forEach {
            if (
                it !is String
            )
                throw IllegalArgumentException("$invalidRequestPrefix invalid neitherEqualStatusFilters")

            parsedNeitherEqualStatusFilters.add(it)
        }
    }

    val parsedNullablesFilters = mutableListOf<NullablesFilterGroup>()

    if (request.contains("nullablesFilters")) {
        if (request["nullablesFilters"] !is List<*>) {
            throw IllegalArgumentException("$invalidRequestPrefix invalid nullablesFilters")
        }

        val requestNullablesFilters = request["nullablesFilters"] as List<*>
        requestNullablesFilters.forEach {
            if (
                it !is LinkedHashMap<*, *>
                || !it.keys.containsAll(listOf("type", "isOrNotNull"))
                || it["type"] !is String
                || it["isOrNotNull"] !is Boolean
                || !NullablesFilterType.isNullablesFilterType(it["type"] as String)
            )
                throw IllegalArgumentException("$invalidRequestPrefix invalid nullablesFilters")

            parsedNullablesFilters.add(NullablesFilterGroup(
                NullablesFilterType.valueOf(it["type"] as String),
                it["isOrNotNull"] as Boolean)
            )
        }
    }

    var parsedHasMassMeasureFilter = false

    if (request.contains("hasMassMeasureFilter")) {
        if (request["hasMassMeasureFilter"] !is Boolean) {
            throw IllegalArgumentException("$invalidRequestPrefix invalid hasMassMeasureFilter")
        }
        parsedHasMassMeasureFilter = request["hasMassMeasureFilter"] as Boolean
    }


    return GetOrderTrackingsByFiltersDto(
        parsedIdFilters,
        parsedTimeFilters,
        parsedEitherEqualStatusFilters,
        parsedNeitherEqualStatusFilters,
        parsedNullablesFilters,
        parsedHasMassMeasureFilter,
        PageRequest.of(page, size)
    )
}
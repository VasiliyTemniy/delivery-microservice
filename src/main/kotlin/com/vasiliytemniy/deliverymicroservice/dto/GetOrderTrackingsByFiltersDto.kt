package com.vasiliytemniy.deliverymicroservice.dto

import com.vasiliytemniy.deliverymicroservice.domain.IdFilterType
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


fun GetOrderTrackingsByFiltersDto.Companion.of(request: OrderTracking.GetPageByFiltersRequest): GetOrderTrackingsByFiltersDto =
    GetOrderTrackingsByFiltersDto(
        request.idFilterGroupsList.toList()
            .map { IdFilterGroup(IdFilterType.valueOf(it.type), it.id) },
        request.timeFilterGroupsList.toList()
            .map { TimeFilterGroup(TimeFilterType.valueOf(it.type), parseOptionalDate(it.from), parseOptionalDate(it.to)) },
        request.eitherEqualStatusFiltersList.toList(),
        request.neitherEqualStatusFiltersList.toList(),
        request.hasMassMeasureFilter,
        PageRequest.of(request.page, request.size)
    )
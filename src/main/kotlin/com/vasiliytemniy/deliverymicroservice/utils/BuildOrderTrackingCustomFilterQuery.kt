package com.vasiliytemniy.deliverymicroservice.utils

import com.vasiliytemniy.deliverymicroservice.domain.OrderTracking
import com.vasiliytemniy.deliverymicroservice.dto.IdFilterGroup
import com.vasiliytemniy.deliverymicroservice.dto.NullablesFilterGroup
import com.vasiliytemniy.deliverymicroservice.dto.TimeFilterGroup
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.isEqual

fun buildOrderTrackingCustomFilterQuery(
    idFilters: List<IdFilterGroup>,
    timeFilters: List<TimeFilterGroup>,
    eitherEqualStatusFilters: List<String>,
    neitherEqualStatusFilters: List<String>,
    nullablesFilters: List<NullablesFilterGroup>,
    hasMassMeasureFilter: Boolean,
): Pair<String, Query> {

    var countQuery = "SELECT count(id) AS total FROM delivery.order_trackings "
    var countQueryPrefix = "WHERE"

    val criteriaList: MutableList<Criteria> = mutableListOf()

    var isFirstFilter = true

    // Apply id filters
    for (it in idFilters) {
        countQuery += "$countQueryPrefix ${it.type} = '${it.id}' "
        criteriaList.add(Criteria.where(it.type.toString()).isEqual(it.id))
        if (isFirstFilter) {
            countQueryPrefix = "AND"
            isFirstFilter = false
        }
    }

    // Apply time filters
    for (it in timeFilters) {
        if (it.from == null && it.to == null) continue
        when {
            it.from != null && it.to != null -> {
                countQuery += "$countQueryPrefix ${it.type} >= '${it.from}' AND ${it.type} <= '${it.to}' "
                criteriaList.add(Criteria.where(it.type.toString()).between(it.from, it.to))
            }
            it.from != null && it.to == null -> {
                countQuery += "$countQueryPrefix ${it.type} >= '${it.from}' "
                criteriaList.add(Criteria.where(it.type.toString()).greaterThan(it.from))
            }
            it.to != null -> {
                countQuery += "$countQueryPrefix ${it.type} <= '${it.to}' "
                criteriaList.add(Criteria.where(it.type.toString()).lessThan(it.to))
            }
        }
        if (isFirstFilter) {
            countQueryPrefix = "AND"
            isFirstFilter = false
        }
    }

    // Block to construct either status query in parentheses
    var eitherStatusQueryModifier = ""
    var eitherStatusQueryPrefix = ""
    val eitherStatusQueryCriteria = Criteria.empty()

    var isFirstEitherStatusModifier = true

    for (it in eitherEqualStatusFilters) {
        eitherStatusQueryModifier += eitherStatusQueryPrefix + "status = '$it' "
        eitherStatusQueryCriteria.or(Criteria.where("status").isEqual(it))
        if (isFirstEitherStatusModifier) {
            isFirstEitherStatusModifier = false
            eitherStatusQueryPrefix = "OR "
        }
    }

    // Apply either status query block to main queries
    if (eitherEqualStatusFilters.isNotEmpty()) {
        countQuery += "$countQueryPrefix (${eitherStatusQueryModifier.trimEnd()}) "
        criteriaList.add(eitherStatusQueryCriteria)
    }

    // Apply neither status filters
    for (it in neitherEqualStatusFilters) {
        countQuery += "$countQueryPrefix status != '$it' "
        criteriaList.add(Criteria.where("status").not(it))
        if (isFirstFilter) {
            countQueryPrefix = "AND"
            isFirstFilter = false
        }
    }

    // Apply nullables filters
    for (it in nullablesFilters) {
        if (it.isOrNotNull) {
            countQuery += "$countQueryPrefix ${it.type} IS NULL "
            criteriaList.add(Criteria.where(it.type.toString()).isNull())
        } else {
            countQuery += "$countQueryPrefix ${it.type} IS NOT NULL "
            criteriaList.add(Criteria.where(it.type.toString()).isNotNull())
        }
    }

    // Apply "hasMassMeasure" filter
    if (hasMassMeasureFilter) {
        countQuery += "$countQueryPrefix mass_measure IS NOT NULL"
        criteriaList.add(Criteria.where(OrderTracking.MASS_MEASURE).isNotNull())
    }

    return countQuery.trimEnd() to Query.query(Criteria.from(criteriaList))
}
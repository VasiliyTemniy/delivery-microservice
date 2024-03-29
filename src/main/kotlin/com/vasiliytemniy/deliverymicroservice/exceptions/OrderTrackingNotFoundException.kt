package com.vasiliytemniy.deliverymicroservice.exceptions

class OrderTrackingNotFoundException: RuntimeException {
    constructor() : super()
    constructor(id: String?, idType: String?) : super("order trackings not found for $idType with $id")
    constructor(id: String?, idType: String?, cause: Throwable?) : super("order trackings not found for $idType with $id", cause)
}
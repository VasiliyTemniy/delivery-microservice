package com.vasiliytemniy.deliverymicroservice.controllers.grpc

import com.vasiliytemniy.deliverymicroservice.domain.DeliveryMeta
import com.vasiliytemniy.deliverymicroservice.domain.toProto
import com.vasiliytemniy.deliverymicroservice.dto.*
import com.vasiliytemniy.deliverymicroservice.interceptors.LogGrpcInterceptor
import com.vasiliytemniy.deliverymicroservice.services.DeliveryMetaService
import com.vasiliytemniy.grpc.deliverymeta.service.DeliveryMeta.*
import com.vasiliytemniy.grpc.deliverymeta.service.DeliveryMetaServiceGrpcKt
import jakarta.validation.ConstraintViolationException
import jakarta.validation.Validator
import kotlinx.coroutines.withTimeout
import net.devh.boot.grpc.server.service.GrpcService
import org.slf4j.LoggerFactory


@GrpcService(interceptors = [LogGrpcInterceptor::class])
class DeliveryMetaGrpcService(
    private val deliveryMetaService: DeliveryMetaService,
    private val validator: Validator
) : DeliveryMetaServiceGrpcKt.DeliveryMetaServiceCoroutineImplBase() {

    override suspend fun calculateDeliveryMeta(
        request: CalculateDeliveryMetaRequest
    ) : CalculateDeliveryMetaResponse =
        withTimeout(TIMEOUT_MILLIS) {
            var error = ""
            var deliveryMeta = DeliveryMeta(null, null)

            try {
                deliveryMeta =
                    deliveryMetaService.calculateDeliveryMeta(validate(CalculateDeliveryMetaDto.of(request)))
            } catch (e: Exception) {
                log.error("calculateDeliveryMeta error: ${e.localizedMessage}")
                error = e.localizedMessage
            }

            if (error != "") {
                return@withTimeout CalculateDeliveryMetaResponse.newBuilder()
                    .setError(error)
                    .setDeliveryMeta(DeliveryMeta(null, null).toProto())
                    .build()
            }

            deliveryMeta
                .let { CalculateDeliveryMetaResponse.newBuilder()
                    .setError(error)
                    .setDeliveryMeta(it.toProto())
                    .build() }
                .also { log.info("calculateDeliveryMeta response: $it") }
        }


    private fun <T> validate(data: T): T {
        return data.run {
            val errors = validator.validate(data)
            if (errors.isNotEmpty()) throw ConstraintViolationException(errors).also { log.error("validation error: ${it.localizedMessage}") }
            data
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(DeliveryMetaGrpcService::class.java)
        const val TIMEOUT_MILLIS = 5000L
    }
}
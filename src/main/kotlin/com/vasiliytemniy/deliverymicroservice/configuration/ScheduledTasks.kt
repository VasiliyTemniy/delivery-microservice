package com.vasiliytemniy.deliverymicroservice.configuration

import com.vasiliytemniy.deliverymicroservice.services.OrderTrackingCoroutineService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled


@Configuration
@EnableScheduling
class ScheduledTasks(
    val orderTrackingCoroutineService: OrderTrackingCoroutineService
) {

    // Double protection to prevent accidental wiping
    private val testEnvironment: String? = System.getProperty("testEnvironment")
    private val enableWipe = testEnvironment != null && testEnvironment == "testProduction"

    @Scheduled(fixedRate = 1000 * 60 * 60 * 24)
    @ConditionalOnProperty(value=["enable.scheduled.wipe"], havingValue = "true", matchIfMissing = false)
    suspend fun wipeAndPopulate() {
        if (enableWipe) {
            orderTrackingCoroutineService.deleteAll()
            orderTrackingCoroutineService.populateGeneratedTestData()
        }
    }

    //TODO Add some health check bot

}
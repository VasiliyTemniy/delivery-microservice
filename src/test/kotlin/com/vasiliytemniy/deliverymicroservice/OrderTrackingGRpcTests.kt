// does not work - needs investigation
// "
// io.grpc.StatusException: INTERNAL: Panic! This is a bug!
// "
// no more error messages

//package com.vasiliytemniy.deliverymicroservice
//
//import com.vasiliytemniy.grpc.ordertracking.service.OrderTracking
//import com.vasiliytemniy.grpc.ordertracking.service.OrderTrackingServiceGrpcKt
//import io.grpc.ManagedChannelBuilder
//import kotlinx.coroutines.runBlocking
//import org.junit.jupiter.api.Test
//import org.springframework.boot.test.context.SpringBootTest
//import java.util.concurrent.TimeUnit
//
//
//@SpringBootTest
//class OrderTrackingGRpcTests {
//
//    @Test
//    fun `should create order tracking`() = runBlocking {
//        val channel = ManagedChannelBuilder.forAddress("localhost", 8000).usePlaintext().build()
//
//        try {
//            val client = OrderTrackingServiceGrpcKt.OrderTrackingServiceCoroutineStub(channel)
//            val request = OrderTracking.CreateRequest.newBuilder()
//                .setOrderId("123")
//                .setPointNumber(1)
//                .setFromFacilityId("123")
//                .setDestinationId("123")
//                .setDestinationType("facility")
//                .setCarrierId("123")
//                .setStatus("transit")
//                .setDeliveryCost(123)
//                .setCurrency("USD")
//                .setCurrencyDecimalMultiplier(100)
//                .setMassControlValue(123)
//                .setMassMeasure("kg")
//                .setEstimatedDeliveryAt("2024-02-01T16:36:43.099912")
//                .setDeliveredAt("2024-02-01T16:36:43.099912")
//                .build()
//            val response = client.create(request)
//
//            println("response: $response")
//
//        } catch (e: Exception) {
//            println("e: $e")
//        } finally {
//            channel.shutdown()
//            channel.awaitTermination(5000, TimeUnit.MILLISECONDS)
//        }
//    }
//}
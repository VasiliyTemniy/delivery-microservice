# delivery-microservice

Side project for m-market-app.

Processes order tracking and calculates delivery metadata.

Uses Spring Boot, Project Reactor, Kotlin coroutines and gRPC.

### Stack
| Field | Tools          |
| --- |----------------|
| Backend | Kotlin + Spring Boot |
| API | gRPC + WebFlux(Project Reactor + Coroutine-based) |
| Database | PostgreSQL + Spring Boot R2DBC |
| Backend cache | Redis |
| CI / (no CD atm) | Github actions |


### Goals

- Implement order tracking and delivery meta calculation microservice for m-market-app.
- Use Project Reactor, Kotlin coroutines and gRPC to study nuances of different Spring Boot api patterns. 

### Controllers:
- OrderTrackingCoroutineCotroller

```
    Routes start with "/api/v1/coroutine/order-tracking": 
    - POST "/"                               returns single Entity
    - GET "/flow/by-order-id/{orderId}"      returns Flow
    - GET "/flow/by-carrier-id/{carrierId}"  returns Flow (optional param filterActive)
    - GET "/by-order-id/{orderId}"           returns Page
    - GET "/by-carrier-id/{carrierId}"       returns Page (optional param filterActive)
    - GET "/by-filters **"                   returns Page (filters accepted as url-encoded JSON via request params)
    - PUT "/status"                          returns List (updates status for multiple entities)
    - PUT "/reorder"                         returns List (swaps pointNumbers accordingly to request body)
    - PUT "/"                                returns single Entity (updates all single order tracking fields)
    - DELETE "/{orderId}"                    returns List (deletes all order trackings by orderId)
    - DELETE "/{orderId}/{pointNumber}"      returns single Entity (deletes single order tracking by external id)
    - DELETE "/all"                          returns Nothing (deletes all order trackings; *)
    - POST "/populate-test-data"             returns Nothing (*)

    * - works only when system property "testEnvironment" is set to "test" or "testProduction"
    ** - has a lot of request params; see src/main/kotlin/com.vasiliytemniy.deliverymicroservice/controllers/OrderTrackingCoroutineController.kt for reference
```

- OrderTrackingReactiveController

```
    Routes start with "/api/v1/reactive/order-tracking":
    Same as OrderTrackingCoroutineController, Flow -> Flux, Page -> Mono<Page>, List -> Mono<List>
    Does not have test methods deleteAll and populate; Reorder not implemented for reactive service
```

- OrderTrackingGrpcService

```
    gRPC service for order tracking
    Has all methods from OrderTrackingCoroutineController
    see src/main/proto/order_tracking.proto for reference
```
- DeliveryMetaController

```
    Routes start with "/api/v1/delivery-meta":
    - GET "*" returns single DeliveryMeta
    * - has a lot of request params; see src/main/kotlin/com.vasiliytemniy.deliverymicroservice/controllers/DeliveryMetaController.kt for reference
```
- DeliveryMetaGrpcService

```
    gRPC service for delivery meta calculation
    Has all methods from DeliveryMetaController
    see src/main/proto/delivery_meta.proto for reference
```


Best regards to [Alexander Bryksin](https://github.com/AleksK1NG); thanks for his guides:
- [Kotlin gRPC with Spring](https://dev.to/aleksk1ng/kotlin-grpc-with-spring-9np)
- [Kotlin Spring WebFlux, R2DBC and Redisson microservice in k8s](https://dev.to/aleksk1ng/kotlin-spring-webflux-r2dbc-and-redisson-microservice-in-k8s-p98)

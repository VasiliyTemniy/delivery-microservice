# delivery-microservice

Side project for m-market-app.

Processes order tracking and calculates delivery metadata.

Uses Spring Boot, Project Reactor, Kotlin coroutines and gRPC.


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
    - GET "/by-filters"                      returns Page (filters accepted as url-encoded JSON via request params)
    - PUT "/status"                          returns List (updates status for multiple entities)
    - PUT "/reorder"                         returns List (swaps pointNumbers accordingly to request body)
    - PUT "/"                                returns single Entity (updates all single order tracking fields)
    - DELETE "/{orderId}"                    returns List (deletes all order trackings by orderId)
    - DELETE "/{orderId}/{pointNumber}"      returns single Entity (deletes single order tracking by external id)
    - DELETE "/all"                          returns Nothing (deletes all order trackings; *)
    - POST "/populate-test-data"             returns Nothing (*)

    * - works only when system property "testEnvironment" is set to "test" or "testProduction"

```

- OrderTrackingReactiveController


    Routes start with "/api/v1/reactive/order-tracking":
    Same as OrderTrackingCoroutineController, Flow -> Flux, Page -> Mono<Page>, List -> Mono<List>
    Does not have test methods deleteAll and populate; Reorder not implemented for reactive service


- OrderTrackingGrpcService


    gRPC service for order tracking
    Has all methods from OrderTrackingCoroutineController
    see src/main/proto/order_tracking.proto for reference

- DeliveryMetaController


    Under construction

- DeliveryMetaGrpcService


    Under construction


### TODO:

- Add dockerization
- Add CI/CD pipeline
- Use external geocoding and geolocation apis
- Use Redis to cache some data
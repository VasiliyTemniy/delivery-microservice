package com.vasiliytemniy.deliverymicroservice.repositories

import org.springframework.stereotype.Repository
import java.time.Duration


@Repository
interface RedisCacheRepository {

    suspend fun setKey(key: String, value: Any)

    suspend fun setKey(key: String, value: Any, ttl: Duration)

    suspend fun <T> getKey(key: String, clazz: Class<T>): T?
}
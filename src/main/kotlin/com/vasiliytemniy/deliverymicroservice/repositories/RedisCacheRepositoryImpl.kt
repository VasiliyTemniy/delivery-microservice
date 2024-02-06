package com.vasiliytemniy.deliverymicroservice.repositories

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.redisson.api.RedissonReactiveClient
import org.redisson.client.codec.StringCodec
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit
import java.time.Duration


@Repository
class RedisCacheRepositoryImpl(
    private val redissonClient: RedissonReactiveClient,
    private val mapper: ObjectMapper,
) : RedisCacheRepository {

    override suspend fun setKey(key: String, value: Any): Unit =
        withContext(Dispatchers.IO) {
            val serializedValue = mapper.writeValueAsString(value)

            redissonClient.getBucket<String>(getKey(key), StringCodec.INSTANCE)
                .set(serializedValue, fixedTtlDuration)
                .awaitSingleOrNull()
                .also { log.info("redis set key: $key, value: $serializedValue") }
        }

    override suspend fun setKey(key: String, value: Any, ttl: Duration): Unit =
        withContext(Dispatchers.IO) {
            
            val serializedValue = mapper.writeValueAsString(value)

            redissonClient.getBucket<String>(getKey(key), StringCodec.INSTANCE)
                .set(serializedValue, ttl)
                .awaitSingleOrNull()
                .also { log.info("redis set key: $key, value: $serializedValue, timeToLive: $ttl") }
        }

    override suspend fun <T> getKey(key: String, clazz: Class<T>): T? =
        withContext(Dispatchers.IO) {
            redissonClient.getBucket<String>(getKey(key), StringCodec.INSTANCE)
                .get()
                .awaitSingleOrNull()?.let {
                    mapper.readValue(it, clazz)
                        .also { value -> log.info("redis get key: $key, value: $value") }
                }
                ?: return@withContext null
        }


    private fun getKey(key: String): String = "$PREFIX:$key"

    companion object {
        private val log = LoggerFactory.getLogger(RedisCacheRepositoryImpl::class.java)
        private val fixedTtlDuration = Duration.parse("P0DT0H30M")
        private const val PREFIX = "delivery-microservice"
    }
}
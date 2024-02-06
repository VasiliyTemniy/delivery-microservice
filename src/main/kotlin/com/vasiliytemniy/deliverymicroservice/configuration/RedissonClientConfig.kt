package com.vasiliytemniy.deliverymicroservice.configuration

import io.github.cdimascio.dotenv.dotenv
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class RedissonClientConfig {

    // Seems like ignore if missing == true does not work while the app is in docker for some reason
    // Thus, I'll put empty .env.docker.fake in the project root with docker instruction to copy and rename to .env
    val dotenvInstance = dotenv { ignoreIfMissing = true }

    @Override
    @Bean
    fun init(): RedissonClient {
        val host = dotenvInstance["REDIS_HOST"]
        val port = dotenvInstance["REDIS_PORT"]
        val timeout = dotenvInstance["REDIS_TIMEOUT"]

        val config = Config()
        val url = "redis://$host:$port"
        config.useSingleServer().setAddress(url).setTimeout(timeout.toInt())
        return Redisson.create(config)
    }
}
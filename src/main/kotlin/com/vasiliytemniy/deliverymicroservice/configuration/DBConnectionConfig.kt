package com.vasiliytemniy.deliverymicroservice.configuration

import io.github.cdimascio.dotenv.dotenv
import io.r2dbc.pool.ConnectionPool
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactoryOptions
import org.flywaydb.core.Flyway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration


@Configuration
class DBConnectionConfig: AbstractR2dbcConfiguration() {

    // Seems like ignore if missing == true does not work while the app is in docker for some reason
    // Thus, I'll put empty .env.docker.fake in the project root with docker instruction to copy and rename to .env
    val dotenvInstance = dotenv { ignoreIfMissing = true }

    @Override
    @Bean
    override fun connectionFactory(): ConnectionPool {
        val kotlinEnv = dotenvInstance["KOTLIN_ENV"]
        val name =
            if (kotlinEnv == "test" || kotlinEnv == "test-prod") dotenvInstance["TEST_R2DBC_NAME"]
            else dotenvInstance["R2DBC_NAME"]
        val password =
            if (kotlinEnv == "test" || kotlinEnv == "test-prod") dotenvInstance["TEST_R2DBC_PASSWORD"]
            else dotenvInstance["R2DBC_PASSWORD"]
        val username =
            if (kotlinEnv == "test" || kotlinEnv == "test-prod") dotenvInstance["TEST_R2DBC_USERNAME"]
            else dotenvInstance["R2DBC_USERNAME"]
        val url =
            if (kotlinEnv == "test" || kotlinEnv == "test-prod") dotenvInstance["TEST_R2DBC_URL"]
            else dotenvInstance["R2DBC_URL"]
        val ssl =
            if (kotlinEnv == "test" || kotlinEnv == "test-prod") dotenvInstance["TEST_R2DBC_SSL"]
            else dotenvInstance["R2DBC_SSL"]
        val maxPoolSize = dotenvInstance["R2DBC_POOL_MAX_SIZE"]
        val initialPoolSize = dotenvInstance["R2DBC_POOL_INITIAL_SIZE"]

        val baseOptions = ConnectionFactoryOptions.parse(url)

        val connectionOptions = ConnectionFactoryOptions.builder().from(baseOptions)
            .option(ConnectionFactoryOptions.DATABASE, name)
            .option(ConnectionFactoryOptions.USER, username)
            .option(ConnectionFactoryOptions.PASSWORD, password)
            .option(ConnectionFactoryOptions.SSL, ssl.toBoolean())
            .build()

        val baseConnectionFactory = ConnectionFactories.get(connectionOptions)

        val poolConfiguration = ConnectionPoolConfiguration.builder(baseConnectionFactory)
            .maxSize(maxPoolSize.toInt())
            .initialSize(initialPoolSize.toInt())
            .build()

        return ConnectionPool(poolConfiguration)
    }

    @Override
    @Bean(initMethod = "migrate")
    fun flyway(): Flyway {
        val kotlinEnv = dotenvInstance["KOTLIN_ENV"]
        val password =
            if (kotlinEnv == "test" || kotlinEnv == "test-prod") dotenvInstance["TEST_R2DBC_PASSWORD"]
            else dotenvInstance["R2DBC_PASSWORD"]
        val username =
            if (kotlinEnv == "test" || kotlinEnv == "test-prod") dotenvInstance["TEST_R2DBC_USERNAME"]
            else dotenvInstance["R2DBC_USERNAME"]
        val url =
            if (kotlinEnv == "test" || kotlinEnv == "test-prod") dotenvInstance["TEST_FLYWAY_URL"]
            else dotenvInstance["FLYWAY_URL"]
        val schema = dotenvInstance["FLYWAY_SCHEMA"]

        return Flyway.configure().dataSource(
            url, username, password
        ).schemas(schema).load()
    }
}
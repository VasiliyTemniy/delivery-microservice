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

    val dotenvInstance = dotenv()

    @Override
    @Bean
    override fun connectionFactory(): ConnectionPool {
        val name = dotenvInstance["R2DBC_NAME"]
        val password = dotenvInstance["R2DBC_PASSWORD"]
        val username = dotenvInstance["R2DBC_USERNAME"]
        val url = dotenvInstance["R2DBC_URL"]
        val maxPoolSize = dotenvInstance["R2DBC_POOL_MAX_SIZE"]
        val initialPoolSize = dotenvInstance["R2DBC_POOL_INITIAL_SIZE"]

        val baseOptions = ConnectionFactoryOptions.parse(url)

        val connectionOptions = ConnectionFactoryOptions.builder().from(baseOptions)
            .option(ConnectionFactoryOptions.DATABASE, name)
            .option(ConnectionFactoryOptions.USER, username)
            .option(ConnectionFactoryOptions.PASSWORD, password)
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
        val password = dotenvInstance["R2DBC_PASSWORD"]
        val username = dotenvInstance["R2DBC_USERNAME"]
        val url = dotenvInstance["FLYWAY_URL"]
        val schema = dotenvInstance["FLYWAY_SCHEMA"]

        return Flyway.configure().dataSource(
            url, username, password
        ).schemas(schema).load()
    }
}
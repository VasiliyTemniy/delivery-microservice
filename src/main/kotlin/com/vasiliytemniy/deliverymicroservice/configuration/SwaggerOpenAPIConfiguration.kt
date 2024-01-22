package com.vasiliytemniy.deliverymicroservice.configuration

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.context.annotation.Configuration


@OpenAPIDefinition(
    info = Info(
        title = "delivery-microservice",
        description = "Delivery microservice",
        contact = Contact(
            name = "Vasiliy Temniy",
            email = "flash_er@mail.ru",
            url = "https://github.com/VasiliyTemniy"
        )
    )
)
@Configuration
class SwaggerOpenAPIConfiguration

package com.melardev.spring.restcrud.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.web.server.WebFilter

@Configuration
class WebConfig {
    @Bean
    fun objectMapperBuilder(): Jackson2ObjectMapperBuilder {
        val builder = Jackson2ObjectMapperBuilder()
        builder.modules(JavaTimeModule())

        // for example: Use created_at instead of createdAt
        builder.propertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)

        // skip null fields
        builder.serializationInclusion(JsonInclude.Include.NON_NULL)
        builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        return builder
    }
/*
    @Autowired
    internal var serverProperties: ServerProperties? = null

    @Bean
    fun contextPathWebFilter(): WebFilter {
        // String contextPath = serverProperties.getServlet().getContextPath();
        val contextPath = "/api"
        return WebFilter { exchange, chain ->
            val request = exchange.getRequest()
            if (request.getURI().getPath().startsWith(contextPath)) {
                chain.filter(
                        exchange.mutate()
                                .request(request.mutate().contextPath(contextPath).build())
                                .build())
            }
            chain.filter(exchange)
        }
    }
    */
}
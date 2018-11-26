package com.pos

import com.pos.domain.EntryCreationDto
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import java.util.concurrent.ConcurrentHashMap


@Configuration
class ProxyConfiguration {

    @Bean("cache")
    fun cache() = ConcurrentHashMap<String, EntryCreationDto>().apply {
        put("prime", EntryCreationDto("Prime", "http://google.com", "<RESPONSE>1", true))
        put("OtherService", EntryCreationDto("OtherService", "http://google.com/2", "<RESPONSE>2", false))
    }


    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }

}
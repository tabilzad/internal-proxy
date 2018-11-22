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
      //  put("Prime", EntryCreationDto("Prime", "google.com", "<RESPONSE>", false))
       // put("OtherService", EntryCreationDto("OtherService", "google.com/2", "<RESPONSE>2", false))
    }


    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }

}
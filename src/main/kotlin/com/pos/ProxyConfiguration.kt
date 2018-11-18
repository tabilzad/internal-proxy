package com.pos

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import java.util.concurrent.ConcurrentHashMap


@Configuration
class ProxyConfiguration {

    @Bean("cache")
    fun cache(): ConcurrentHashMap<String, String> = ConcurrentHashMap<String, String>().apply {
      //  put("prime", "this is a mocked response")
    }


    @Bean("routes")
    fun routes(): ConcurrentHashMap<String, String> {
        return ConcurrentHashMap<String, String>().apply {
            put("prime", "http://www.thomas-bayer.com/axis2/services/BLZService")
        }
    }


    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }

}
package com.pos

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cloud.gateway.mvc.ProxyExchange
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.concurrent.ConcurrentHashMap


@Service
class ProxyService(
    @Qualifier("cache") val cache: ConcurrentHashMap<String, String>,
    @Qualifier("routes") val routes: ConcurrentHashMap<String, String>
) {
    fun forwardOrMockPost(exchage: ProxyExchange<Any>, serviceName: String, body: String): Any {
        return cache[serviceName] ?: (routes[serviceName]?.let {
            println("Forwarding to $it")
            RestTemplate().exchange(it, HttpMethod.POST, HttpEntity(body).apply {
                headers.accept = listOf(MediaType.TEXT_PLAIN)
            }, String::class.java)
            //exchage.uri(it).body(body).post()
        } ?: "This Service Name is unknown")
    }

    fun forwardOrMockGet(
        exchage: ProxyExchange<Any>,
        serviceName: String
    ): Any {
        return cache[serviceName] ?: (routes[serviceName]?.let {
            exchage.uri(it).get()
        } ?: "This Service Name is unknown")
    }

    fun mock(serviceName: String, body: String) {
        cache[serviceName] = body
    }

    fun configure(serviceName: String, url: String) {
        routes[serviceName] = url
    }

    fun clearAll() {
        cache.clear()
    }

    fun show() = cache to routes
}


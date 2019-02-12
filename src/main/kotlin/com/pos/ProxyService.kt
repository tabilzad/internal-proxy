package com.pos

import com.pos.domain.EntryCreationDto
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cloud.gateway.mvc.ProxyExchange
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.concurrent.ConcurrentHashMap
import javax.servlet.http.HttpServletRequest


@Service
class ProxyService(
    @Qualifier("cache") val cache: ConcurrentHashMap<String, EntryCreationDto>,
    val template: RestTemplate
) {
    fun forwardOrMockPost(rawRequest: HttpServletRequest, serviceName: String, body: String): ResponseEntity<*> {
        return cache[serviceName]?.let {
            when (it.mocked) {
                true -> ResponseEntity.status(it.status).body(it.mock)
                false -> template.exchange(
                    it.realUrl + rawRequest.buildParams(),
                    HttpMethod.POST,
                    HttpEntity<Any>(rawRequest.buildHeaders()),
                    String::class.java
                )
            }
        } ?: ResponseEntity.status(HttpStatus.NO_CONTENT).body("This Service Name is unknown: $serviceName")
    }

    fun forwardOrMockGet(
        rawRequest: HttpServletRequest,
        serviceName: String
    ): ResponseEntity<*> {

        val headers = rawRequest.buildHeaders()
        val params = rawRequest.buildParams()

        return cache[serviceName]?.let {
            when (it.mocked) {
                true -> ResponseEntity.status(it.status).body(it.mock)
                false -> template.exchange(
                    it.realUrl + params,
                    HttpMethod.GET,
                    HttpEntity<Any>(headers),
                    String::class.java
                )
            }
        } ?: ResponseEntity.status(HttpStatus.NO_CONTENT).body("This Service Name is unknown: $serviceName")
    }

    fun mock(serviceName: String, body: String) {
        cache[serviceName]?.let {
            cache[serviceName] = it.copy(mock = body)
        } ?: let { cache[serviceName] = EntryCreationDto(name = serviceName, mock = body, mocked = true) }
    }

    fun configure(serviceName: String, url: String) {
        cache[serviceName]?.let {
            cache[serviceName] = it.copy(realUrl = url)
        } ?: let { cache[serviceName] = EntryCreationDto(name = serviceName, realUrl = url, mocked = false) }
    }

    fun clearAll() {
        cache.clear()
    }

    fun show() = cache

    fun HttpServletRequest.buildHeaders(): HttpHeaders = HttpHeaders().apply {
        headerNames.iterator().forEach {
            set(it, getHeader(it))
        }
    }

    fun HttpServletRequest.buildParams() = parameterMap.map { (key, value) ->
        key to value.first()
    }.joinToString { (key, value) -> "?$key=$value" }
}


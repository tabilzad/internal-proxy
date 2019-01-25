package com.pos

import com.pos.domain.EntryCreationDto
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cloud.gateway.mvc.ProxyExchange
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap


@Service
class ProxyService(
    @Qualifier("cache") val cache: ConcurrentHashMap<String, EntryCreationDto>
) {
    fun forwardOrMockPost(exchage: ProxyExchange<Any>, serviceName: String, body: String): ResponseEntity<*> {
        return cache[serviceName]?.let {
            when (it.mocked) {
                true -> ResponseEntity.status(it.status).body(it.mock)
                false -> exchage.uri(it.realUrl).body(body).post()
            }
        } ?: ResponseEntity.status(HttpStatus.NO_CONTENT).body("This Service Name is unknown: $serviceName")
    }

    fun forwardOrMockGet(
        exchage: ProxyExchange<Any>,
        serviceName: String,
        extraParams: String?
    ): ResponseEntity<*> {
        return cache[serviceName]?.let {
            when (it.mocked) {
                true -> ResponseEntity.status(it.status).body(it.mock)
                false -> exchage.uri(it.realUrl+"/"+extraParams).get()
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
}


package com.pos

import com.pos.domain.EntryCreationDto
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cloud.gateway.mvc.ProxyExchange
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap


@Service
class ProxyService(
    @Qualifier("cache") val cache: ConcurrentHashMap<String, EntryCreationDto>
) {
    fun forwardOrMockPost(exchage: ProxyExchange<Any>, serviceName: String, body: String): String {
        return cache[serviceName]?.let {
            println("Forwarding to $it")
            //RestTemplate().exchange(it, HttpMethod.POST, HttpEntity(body).apply {
            //  headers.accept = listOf(MediaType.TEXT_PLAIN)
            //}, String::class.java)
            when(it.mocked){
                true->it.mock
                false->exchage.uri(it.realUrl).body(body).post().toString()
            }

        } ?: "This Service Name is unknown"
    }

    fun forwardOrMockGet(
        exchage: ProxyExchange<Any>,
        serviceName: String
    ): String {
        return cache[serviceName]?.let {
            when(it.mocked){
                true->it.mock
                false->exchage.uri(it.realUrl).get().toString()
            }

        } ?: "This Service Name is unknown"
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


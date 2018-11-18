package com.pos

import org.springframework.cloud.gateway.mvc.ProxyExchange
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import java.util.concurrent.ConcurrentHashMap


@RestController
class ProxyController(
    val router: ProxyService
) {


    @GetMapping("/call/{name}")
    @Throws(Exception::class)
    fun proxyGet(proxy: ProxyExchange<Any>, @PathVariable name: String): ResponseEntity<*> {
        return ResponseEntity.ok(router.forwardOrMockGet(proxy, name))
    }


    @PostMapping("/call/{name}")
    @Throws(Exception::class)
    fun proxPost(proxy: ProxyExchange<Any>, @PathVariable name: String, @RequestBody body: String): ResponseEntity<*> {
        return ResponseEntity.ok(router.forwardOrMockPost(proxy, name, body))
    }


    @PostMapping("/configure/{serviceName}")
    fun configure(
        @PathVariable serviceName: String,
        @RequestBody url: String
    ): String {
        router.configure(serviceName, url)
        return "Routed $serviceName to $url"
    }

    @PostMapping("/mock/{serviceName}")
    fun mock(
        @PathVariable serviceName: String,
        @RequestBody body: String
    ): String {
        router.mock(serviceName, body)
        return "Mocked $serviceName"
    }

    @GetMapping("/clear/all")
    fun mock(): String {
        router.clearAll()
        return "Cleared"
    }

    @GetMapping("/show")
    fun show(): Pair<ConcurrentHashMap<String, String>, ConcurrentHashMap<String, String>> {
        return router.show()
    }
}
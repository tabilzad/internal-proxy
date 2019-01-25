package com.pos.controllers

import com.pos.ProxyService
import com.pos.domain.EntryCreationDto
import org.springframework.cloud.gateway.mvc.ProxyExchange
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.concurrent.ConcurrentHashMap

@RestController
@RequestMapping("/proxy")
class ProxyController(
    val router: ProxyService
) {
    @GetMapping(value = ["/call/{name}", "/call/{name}/{extra}"])
    @Throws(Exception::class)
    fun proxyGet(proxy: ProxyExchange<Any>, @PathVariable name: String, @PathVariable extra: String? = ""): ResponseEntity<*> {
        return router.forwardOrMockGet(proxy, name.toLowerCase())
    }

    @GetMapping("/show")
    fun show(): ConcurrentHashMap<String, EntryCreationDto> = router.show()

    @PostMapping("/call/{name}")
    @Throws(Exception::class)
    fun proxPost(proxy: ProxyExchange<Any>, @PathVariable name: String, @RequestBody body: String): ResponseEntity<*> {
        return router.forwardOrMockPost(proxy, name.toLowerCase(), body)
    }

    @PostMapping("/configure/{serviceName}")
    fun configure(
        @PathVariable serviceName: String,
        @RequestBody url: String
    ): String {
        router.configure(serviceName.toLowerCase(), url)
        return "Routed $serviceName to $url"
    }

    @PostMapping("/mock/{serviceName}")
    fun mock(
        @PathVariable serviceName: String,
        @RequestBody body: String
    ): String {
        router.mock(serviceName.toLowerCase(), body)
        return "Mocked $serviceName"
    }

    @GetMapping("/clear/all")
    fun mock(): String {
        router.clearAll()
        return "Cleared"
    }
}
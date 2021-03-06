package com.pos.controllers

import com.pos.domain.EntryCreationDto
import com.pos.services.ProxyService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.concurrent.ConcurrentHashMap
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/proxy")
class ProxyController(
    val router: ProxyService
) {
    @GetMapping(value = ["/call/{name}", "/call/{name}/**"])
    @Throws(Exception::class)
    fun proxyGet(request: HttpServletRequest, @PathVariable name: String, @PathVariable extra: String? = ""): ResponseEntity<*> {
        return router.forwardOrMockGet(request, name.toLowerCase(), extra ?: "")
    }

    @GetMapping("/show")
    fun show(): ConcurrentHashMap<String, EntryCreationDto> = router.show()

    @PostMapping(value = ["/call/{name}", "/call/{name}/**"])
    @Throws(Exception::class)
    fun proxPost(request: HttpServletRequest, @PathVariable name: String, @RequestBody body: String): ResponseEntity<*> {
        return router.forwardOrMockPost(request, name.toLowerCase(), body)
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
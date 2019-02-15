package com.pos.services

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientResponseException

@Service
class ProxyErrorHandler {
    operator fun invoke(call: () -> ResponseEntity<*>): ResponseEntity<*> {
        return try {
            call()
        } catch (ex: RestClientResponseException) {
            ResponseEntity
                .status(ex.rawStatusCode)
                .contentType(ex.responseHeaders?.contentType ?: MediaType.TEXT_PLAIN)
                .body(ex.responseBodyAsString)
        }
    }
}
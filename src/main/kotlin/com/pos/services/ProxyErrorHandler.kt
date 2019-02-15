package com.pos.services

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientResponseException

@Service
class ProxyErrorHandler(val logger: LoggingService) {
    operator fun invoke(call: () -> ResponseEntity<*>): ResponseEntity<*> {
        return try {
            call().also {
                logger.log("Forward Success with status ${it.statusCode}")
            }
        } catch (ex: RestClientResponseException) {
            logger.logError("Forward Failure due to ${ex.rawStatusCode} and ${ex.responseBodyAsString}")
            ResponseEntity
                .status(ex.rawStatusCode)
                .contentType(ex.responseHeaders?.contentType ?: MediaType.TEXT_PLAIN)
                .body(ex.responseBodyAsString)
        }
    }
}
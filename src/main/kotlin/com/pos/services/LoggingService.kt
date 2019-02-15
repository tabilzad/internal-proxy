package com.pos.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class LoggingService {

    private val logger = LoggerFactory.getILoggerFactory().getLogger("PROXY LOGGER")

    fun log(message: String) {
        logger.info(message)
    }

    fun logError(message: String) {
        logger.error(message)
    }

}
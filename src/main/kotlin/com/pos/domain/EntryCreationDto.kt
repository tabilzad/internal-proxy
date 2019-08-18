package com.pos.domain

import org.springframework.http.HttpStatus

data class EntryCreationDto(
    var name: String = "",
    var realUrl: String = "",
    var mock: String = "",
    var mocked: Boolean = false,
    var status: HttpStatus = HttpStatus.OK,
    var callCount: Long = 0,
    var delay: Long = 0
)

data class ConnectionEntry(
    var ip: String = "",
    var port: Int = 8090
)



package com.pos.domain

import org.springframework.http.HttpStatus

data class EntryCreationDto(
    var name: String = "",
    var realUrl: String = "",
    var mock: String = "",
    var mocked: Boolean = false,
    var status: HttpStatus = HttpStatus.OK
)

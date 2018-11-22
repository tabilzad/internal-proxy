package com.pos.domain

data class EntryCreationDto(
    var name: String = "",
    var realUrl: String = "",
    var mock: String = "",
    var mocked: Boolean = false
)

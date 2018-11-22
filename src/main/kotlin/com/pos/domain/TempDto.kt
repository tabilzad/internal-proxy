package com.pos.domain

data class TempDto(
    val temp: List<EntryCreationDto> = mutableListOf()
)
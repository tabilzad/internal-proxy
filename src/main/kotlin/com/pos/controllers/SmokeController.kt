package com.pos.controllers

import org.springframework.http.MediaType
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SmokeController {

    //TODO("")
    @GetMapping("/smoke/get", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun show(): String {
        return data
    }

    companion object {
        val data = "";
    }

}
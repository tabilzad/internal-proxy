package com.pos

import org.springframework.boot.info.BuildProperties
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ModelAttribute


@ControllerAdvice
class AppVersionController(val props: BuildProperties) {

    @ModelAttribute("appVersion")
    fun getVersion(): String = props.version
}
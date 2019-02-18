package com.pos.controllers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.pos.domain.EntryCreationDto
import com.pos.domain.TempDto
import com.pos.services.ProxyService
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap


@Controller
class FormsController(
    val router: ProxyService,
    val cache: ConcurrentHashMap<String, EntryCreationDto>
) {

    @RequestMapping("/")
    fun home(): String {
        return "redirect:/all"
    }

    @GetMapping("/all")
    fun show(model: Model): String {
        model.addAttribute("entries", cache)
        return "entries/allEntries"
    }


    @GetMapping("/create")
    fun showCreateForm(model: Model): String {
        val new = EntryCreationDto()
        model.addAttribute("form", new)
        return "entries/add"
    }

    @PostMapping("/save")
    fun save(@ModelAttribute form: EntryCreationDto, model: Model): String {
        cache.putAll(mapOf(form.name.friendlyForm() to form))
        model.addAttribute("entries", router.show())
        return "redirect:/all"
    }

    @GetMapping("/delete/{service}")
    fun delete(@PathVariable service: String, model: Model): String {
        cache.remove(service)
        model.addAttribute("entries", router.show())
        return "redirect:/edit"
    }


    @GetMapping("/edit")
    fun showEditForm(model: Model): String {
        val tempDto = TempDto(cache.map { (key, value) ->
            value.copy(
                name = key.friendlyForm()
            )
        })
        model.addAttribute("wrapper", tempDto)

        return "entries/edit"
    }

    @PostMapping("/replace")
    fun replace(@ModelAttribute wrapper: TempDto, model: Model): String {
        val counts = cache.map { it.key to it.value.callCount }
        cache.clear()
        wrapper.temp.forEach { value ->
            cache[value.name] = value.copy(callCount = counts.find { it.first == value.name }?.second ?: 0)
        }

        model.addAttribute("wrapper", wrapper)
        return "redirect:/all"
    }

    @GetMapping("/clear")
    fun saveBooks(model: Model): String {
        router.clearAll()
        return "redirect:/all"
    }

    @GetMapping("/login")
    fun logingError(@RequestParam(value = "error", required = false) error: Boolean, model: Model): String {
        if (error) model.addAttribute("loginError", true)
        return "login"
    }

    @GetMapping("/upload")
    fun import(model: Model): String {
        return "entries/upload"
    }


    @GetMapping("/export")
    @ResponseBody
    fun export(model: Model): ResponseEntity<FileSystemResource> {
        val down = FileSystemResource(File("ip_profile_${LocalDate.now()}").apply {
            writeText(jacksonObjectMapper().writeValueAsString(cache.values))
        }.also { it.deleteOnExit() })

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${down.filename}")
            .body(down)
    }

    @PostMapping("/uploadFile")
    fun submit(@RequestParam("file") file: MultipartFile, model: ModelMap): String {
        model.addAttribute("file", file)
        cache.clear()
        jacksonObjectMapper().run {
            readValue<List<EntryCreationDto>>(
                file.bytes,
                typeFactory.constructCollectionType(List::class.java, EntryCreationDto::class.java)
            )
        }.forEach {
            cache[it.name] = it
        }
        return "redirect:/all"
    }


    fun String.friendlyForm() = this.toLowerCase().replace(" ", "_")
}

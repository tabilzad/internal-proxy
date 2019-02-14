package com.pos.controllers

import com.pos.ProxyService
import com.pos.domain.EntryCreationDto
import com.pos.domain.TempDto
import org.springframework.ui.Model
import java.util.concurrent.ConcurrentHashMap
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

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

        model.addAttribute("wrapper", wrapper.copy(temp = wrapper.temp.sortedBy { it.name }))
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


    fun String.friendlyForm() = this.toLowerCase().replace(" ", "_")
}

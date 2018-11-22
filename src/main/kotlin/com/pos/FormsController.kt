package com.pos

import com.pos.domain.EntryCreationDto
import com.pos.domain.TempDto
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import java.util.concurrent.ConcurrentHashMap
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.stereotype.Controller

@Controller
class FormsController(
    val router: ProxyService,
    val cache: ConcurrentHashMap<String, EntryCreationDto>
) {

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
    fun saveBooks(@ModelAttribute form: EntryCreationDto, model: Model): String {
        cache.putAll(mapOf(form.name to form))
        model.addAttribute("entries", router.show())
        return "redirect:/all"
    }


    @GetMapping("/edit")
    fun showEditForm(model: Model): String {
        val tempDto = TempDto(cache.map { (key, value) ->
            EntryCreationDto(name = key, realUrl = value.realUrl, mock = value.mock, mocked = value.mocked)
        })
        model.addAttribute("wrapper", tempDto)

        return "entries/edit"
    }

    @PostMapping("/replace")
    fun replace(@ModelAttribute wrapper: TempDto, model: Model): String {
        cache.clear()
        cache.putAll(mapOf(*wrapper.temp.map { it.name to it }.toTypedArray()))
        model.addAttribute("wrapper", wrapper)
        return "redirect:/all"
    }

    @GetMapping("/clear")
    fun saveBooks(model: Model): String {
        router.clearAll()
        return "redirect:/all"
    }
}
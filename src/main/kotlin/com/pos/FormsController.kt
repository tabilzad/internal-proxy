package com.pos

import com.pos.domain.EntryCreationDto
import com.pos.domain.TempDto
import org.springframework.ui.Model
import java.util.concurrent.ConcurrentHashMap
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*


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
        return "redirect:/all"
    }


    @GetMapping("/edit")
    fun showEditForm(model: Model): String {
        val tempDto = TempDto(cache.map { (key, value) ->
            EntryCreationDto(
                name = key.friendlyForm(),
                realUrl = value.realUrl,
                mock = value.mock,
                mocked = value.mocked
            )
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

    fun String.friendlyForm() = this.toLowerCase().replace(" ", "_")
}

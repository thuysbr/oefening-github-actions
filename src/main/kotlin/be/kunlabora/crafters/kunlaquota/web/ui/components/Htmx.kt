package be.kunlabora.crafters.kunlaquota.web.ui.components

import kotlinx.html.Tag

object Htmx {
    var Tag.hxPost: String
        get() = error("not implemented")
        set(value) {
            attributes["hx-post"] = value
        }
    var Tag.hxTarget: String
        get() = error("not implemented")
        set(value) {
            attributes["hx-target"] = value
        }
    var Tag.hxSwap: String
        get() = error("not implemented")
        set(value) {
            attributes["hx-swap"] = value
        }
}
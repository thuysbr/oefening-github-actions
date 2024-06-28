package be.kunlabora.crafters.kunlaquota.web.ui.components

import be.kunlabora.crafters.kunlaquota.web.ui.baseUiUrl
import be.kunlabora.crafters.kunlaquota.web.ui.components.Htmx.hxPost
import be.kunlabora.crafters.kunlaquota.web.ui.components.Htmx.hxSwap
import be.kunlabora.crafters.kunlaquota.web.ui.components.Htmx.hxTarget
import be.kunlabora.crafters.kunlaquota.web.ui.path
import kotlinx.html.*

object NewQuoteScreen {
    fun FlowContent.showNewQuote() {
        div("box") {
            form {
                quoteLine()
                div("field") {
                    id = "extraLine"
                }
                div("field is-grouped") {
                    div("control") {
                        button(classes = "button is-link") {
                            type = ButtonType.button
                            hxPost = baseUiUrl.path("new", "addLine")
                            hxTarget = "#extraLine"
                            hxSwap = "beforeend"
                            +"Add Line"
                        }
                    }
                    div("control") {
                        button(classes = "button is-primary") {
                            type = ButtonType.submit
                            hxPost = baseUiUrl.path("new")
                            hxSwap = "none"
                            +"Submit"
                        }
                    }
                }
            }
        }
    }

    fun FlowContent.quoteLine() {
        div("field is-grouped columns") {
            div("control column is-one-fifth") {
                input(InputType.text, classes = "input", name = "nameInput") {
                    placeholder = "Name"
                }
            }
            div("control column") {
                input(InputType.text, classes = "input", name = "textInput") {
                    placeholder = "Text"
                }
            }
        }
    }
}
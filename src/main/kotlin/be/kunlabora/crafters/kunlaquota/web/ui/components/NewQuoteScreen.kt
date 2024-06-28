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
                hxTarget = "#errorMessages"
                hxSwap = "innerHTML"
                hxPost = baseUiUrl.path("new")

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
                            +"Submit"
                        }
                    }
                }
            }
        }
    }

    fun FlowContent.quoteLine() {
        div("field is-grouped") {
            div("control") {
                input(InputType.text, classes = "input", name = "nameInput") {
                    placeholder = "Name"
                }
            }
            div("control is-expanded") {
                input(InputType.text, classes = "input", name = "textInput") {
                    placeholder = "Text"
                }
            }
        }
    }

    fun FlowContent.errorMessage(vararg messages: String) {
        article("message is-danger") {
            div(classes = "message-body") {
                messages.forEach { message ->
                    p { +message }
                }
            }
        }
    }
}
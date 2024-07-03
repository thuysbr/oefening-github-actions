package be.kunlabora.crafters.kunlaquota.web.ui.screens

import be.kunlabora.crafters.kunlaquota.web.ui.baseUiUrl
import be.kunlabora.crafters.kunlaquota.web.ui.components.Htmx.hxPost
import be.kunlabora.crafters.kunlaquota.web.ui.components.Htmx.hxSwap
import be.kunlabora.crafters.kunlaquota.web.ui.components.Htmx.hxTarget
import be.kunlabora.crafters.kunlaquota.web.ui.components.Htmx.hyper
import be.kunlabora.crafters.kunlaquota.web.ui.path
import kotlinx.html.*

object AddQuoteModal {
    const val addQuoteModalId = "addquotemodal"

    fun FlowContent.addQuoteModal() {
        div(classes = "modal") {
            id = addQuoteModalId
            div(classes = "modal-background")
            div(classes = "modal-card") {
                header(classes = "modal-card-head") {
                    p(classes = "modal-card-title") { +"Add a Quote" }
                    button(classes = "delete") {
                        id = "delete"
                        attributes["aria-label"] = "close"
                        hyper = "on click take .is-active from $addQuoteModalId wait 200ms then remove $addQuoteModalId"
                    }
                }
                section(classes = "modal-card-body") {
                    form {
                        hxTarget = "#errorMessages"
                        hxSwap = "innerHTML"
                        hxPost = baseUiUrl.path("new")
                        hyper = "on submit take .is-active from $addQuoteModalId"
                        addQuoteLine()

                        div("field") {
                            id = "extraLine"
                        }

                        div("control") {
                            button(classes = "button is-link") {
                                type = ButtonType.button
                                hxPost = baseUiUrl.path("new", "addLine")
                                hxTarget = "#extraLine"
                                hxSwap = "beforeend"
                                +"Add Line"
                            }
                        }
                        footer(classes = "modal-card-foot") {
                            button(classes = "button is-primary is-success") {
                                type = ButtonType.submit
                                +"Save"
                            }
                            button(classes = "button") {
                                type = ButtonType.button
                                hyper =
                                    "on click take .is-active from $addQuoteModalId wait 200ms then remove $addQuoteModalId"
                                +"Cancel"
                            }
                        }
                    }
                }
            }
        }
    }

    fun FlowContent.addQuoteLine() {
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
}
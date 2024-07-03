package be.kunlabora.crafters.kunlaquota.web.ui.components

import be.kunlabora.crafters.kunlaquota.service.IQuotes
import be.kunlabora.crafters.kunlaquota.service.domain.Quote
import be.kunlabora.crafters.kunlaquota.web.ui.baseUiUrl
import be.kunlabora.crafters.kunlaquota.web.ui.components.Htmx.hxPost
import be.kunlabora.crafters.kunlaquota.web.ui.components.Htmx.hxSwap
import be.kunlabora.crafters.kunlaquota.web.ui.components.Htmx.hxTarget
import be.kunlabora.crafters.kunlaquota.web.ui.components.Util.formatToKunlaDate
import be.kunlabora.crafters.kunlaquota.web.ui.path
import kotlinx.html.*
import java.time.LocalDateTime

object ShowQuotesScreen {
    fun FlowContent.showQuotes(quotes: IQuotes) {
        section(classes = "section") {
            id = "quote-section"
            div(classes = "container") {
                div(classes = "block") { id = "errorMessages" }
                quotes.findAll()
                    .map { fetchedQuotes -> fetchedQuotes.forEach { quote2(it) } }
            }
        }

        button(classes = "button is-primary") {
            id = "add-quote"
            style = "position: fixed; bottom: 20px; right: 20px;"
            //href = baseUiUrl.path("new")
            onClick = "document.querySelector('dialog').showModal()"
            +"Add Quote"
        }

        div {
            id = "add-quote-modal"
            div(classes = "modal-card") {
                header(classes = "modal-card-head") {
                    p(classes = "modal-card-title") { +"Add a Quote" }
                    button { id = "delete" }
                }
                section(classes = "modal-card-body") {
                    form {
                        hxTarget = "#errorMessages"
                        hxSwap = "innerHTML"
                        hxPost = baseUiUrl.path("new")

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
                    }
                }
                footer(classes = "modal-card-foot") {
                    button(classes = "button is-primary is-success") {
                        type = ButtonType.submit
                        +"Save"
                    }
                    button(classes = "button") {
                        type = ButtonType.button
                        +"Cancel"
                    }
                }
            }
        }
        script {
            unsafe {
                +"""
        // JavaScript to handle modal
        const modal = document.getElementById('add-quote-modal');
        const addQuoteButton = document.getElementById('add-quote');
        const deleteButton = document.getElementById('delete');

        addQuoteButton.addEventListener('click', () => {
            modal.showModal();
        });

        deleteButton.addEventListener('click', () => {
            modal.hide();
        });                
            """.trimIndent()
            }
        }
    }

    private fun FlowContent.addQuoteLine() {
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


    private fun FlowContent.quote2(quote: Quote) {
        div("card") {
            div("card-content") {
                quote.lines.forEach { quoteLine(it); br }
                p(classes = "subtitle") {
                    quoteDate(quote.at)
                }
            }
        }
    }

    private fun FlowContent.quoteLine(line: Quote.Line) {
        p {
            strong { +"${line.name}: " }
            +line.text
        }
    }

    private fun FlowContent.quoteDate(at: LocalDateTime) {
        div("is-flex is-justify-content-flex-end") {
            p("is-size-7 has-text-grey-light") {
                +at.formatToKunlaDate()
            }
        }
    }
}
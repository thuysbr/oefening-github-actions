package be.kunlabora.crafters.kunlaquota.web.ui.screens

import be.kunlabora.crafters.kunlaquota.web.ui.components.Htmx.hyper
import kotlinx.html.*
import java.net.URI

object ShareQuoteModal {
    const val shareQuoteModalId = "sharequotemodal"

    fun FlowContent.shareQuoteModal(sharedQuoteUrl: URI) {
        div(classes = "modal") {
            id = shareQuoteModalId
            div(classes = "modal-background")
            div(classes = "modal-card") {
                style = "border-radius: 8px;" // Ensure consistent rounded corners
                header()
                content(sharedQuoteUrl.toString())
                footer()
            }
        }
    }

    private fun DIV.content(sharedQuoteUrl: String) {
        section(classes = "modal-card-body") {
            p {
                +"Copy paste the following link: $sharedQuoteUrl"
            }
        }
    }

    private fun FlowContent.footer() {
        footer(classes = "modal-card-foot is-justify-content-end") {
            style = "border-top: none; display: flex; justify-content: flex-end; width: 100%;"
            cancelButton()
        }
    }

    private val removeModalHypertext =
        "on click take .is-active from $shareQuoteModalId wait 200ms then remove $shareQuoteModalId"

    private fun FOOTER.cancelButton() {
        button(classes = "button") {
            type = ButtonType.button
            hyper = removeModalHypertext
            +"Ok"
        }
    }

    private fun HEADER.closeButton() {
        button(classes = "delete") {
            id = "delete"
            attributes["aria-label"] = "close"
            hyper = removeModalHypertext
        }
    }

    private fun DIV.header() {
        header(classes = "modal-card-head") {
            style = "border-bottom: none;" // Remove the gradient
            p(classes = "modal-card-title") { +"Share this link with your friends" }
            closeButton()
        }
    }
}
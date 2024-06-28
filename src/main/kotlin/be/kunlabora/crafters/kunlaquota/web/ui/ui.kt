package be.kunlabora.crafters.kunlaquota.web.ui

import be.kunlabora.crafters.kunlaquota.service.AddQuote
import be.kunlabora.crafters.kunlaquota.service.IQuotes
import be.kunlabora.crafters.kunlaquota.service.domain.Quote
import be.kunlabora.crafters.kunlaquota.service.get
import be.kunlabora.crafters.kunlaquota.web.ui.components.NavBar.navbar
import be.kunlabora.crafters.kunlaquota.web.ui.components.NewQuoteScreen.errorMessage
import be.kunlabora.crafters.kunlaquota.web.ui.components.NewQuoteScreen.quoteLine
import be.kunlabora.crafters.kunlaquota.web.ui.components.NewQuoteScreen.showNewQuote
import be.kunlabora.crafters.kunlaquota.web.ui.components.ShowQuotesScreen.showQuotes
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.servlet.function.RouterFunctionDsl
import org.springframework.web.servlet.function.ServerResponse
import java.io.StringWriter

const val baseUiUrl = "/ui"

fun uiRoutes(quotes: IQuotes): RouterFunctionDsl.() -> Unit = {
    GET("") {
        val title = "KunlaQuota"
        ServerResponse.status(HttpStatus.OK)
            .contentType(MediaType.TEXT_HTML)
            .body(
                wrapper(title) {
                    showQuotes(quotes)
                }
            )
    }
    GET("new") {
        val title = "Add new Quote"
        ServerResponse.status(HttpStatus.OK)
            .contentType(MediaType.TEXT_HTML)
            .body(
                wrapper(title) {
                    showNewQuote()
                }
            )
    }
    POST("new/addLine") {
        ServerResponse.status(HttpStatus.OK)
            .body(
                partial { quoteLine() }
            )
    }
    POST("new") { request ->
        val names = request.params()["nameInput"] ?: emptyList()
        val texts = request.params()["textInput"] ?: emptyList()

        val addQuote = AddQuote(
            names.zip(texts).mapIndexed { idx, (name, text) -> Quote.Line(idx, name, text) }
        )
        quotes.execute(addQuote)
            .map {
                ServerResponse.status(HttpStatus.OK)
                    .header("HX-Redirect", baseUiUrl)
                    .build()
            }.recover { failure ->
                ServerResponse.status(HttpStatus.OK)
                    .body(partial { errorMessage("Oopsie! Something broke!", failure.message) })
            }.get()
    }

}

fun wrapper(title: String, block: DIV.() -> Unit) =
    StringWriter().appendHTML().html {
        head {
            title { +title }
            meta(charset = "utf-8")
            meta(name = "viewport", content = "width=device-width, initial-scale=1")
            link(
                rel = "stylesheet",
                href = "https://cdn.jsdelivr.net/npm/bulma@1.0.1/css/versions/bulma-no-dark-mode.min.css"
            )
            script(src = "https://unpkg.com/htmx.org@2.0.0") {}
        }
        body {
            navbar()
            section(classes = "section") {
                div(classes = "container") {
                    div(classes = "block") { id = "errorMessages" }
                    block()
                }
            }
        }
    }.toString()

fun partial(block: DIV.() -> Unit) = StringWriter().appendHTML().div { block() }.toString()


fun String.path(vararg paths: String): String =
    buildList {
        add(this@path)
        addAll(paths)
    }.joinToString("/")
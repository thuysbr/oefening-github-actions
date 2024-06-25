package be.kunlabora.crafters.kunlaquota.web.ui

import be.kunlabora.crafters.kunlaquota.service.IQuotes
import be.kunlabora.crafters.kunlaquota.web.ui.components.NavBar.navbar
import be.kunlabora.crafters.kunlaquota.web.ui.components.ShowQuotesScreen.showQuotes
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.servlet.function.RouterFunctionDsl
import org.springframework.web.servlet.function.ServerResponse
import java.io.StringWriter

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
        }
        body {
            navbar()
            section(classes = "section") {
                div(classes = "container") {
                    block()
                }
            }
        }
    }.toString()

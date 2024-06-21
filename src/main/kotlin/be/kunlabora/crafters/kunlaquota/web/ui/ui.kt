package be.kunlabora.crafters.kunlaquota.web.ui

import be.kunlabora.crafters.kunlaquota.service.IQuotes
import be.kunlabora.crafters.kunlaquota.service.domain.Quote
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.servlet.function.RouterFunctionDsl
import org.springframework.web.servlet.function.ServerResponse
import java.io.StringWriter
import java.time.format.DateTimeFormatter

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
            link(rel = "stylesheet", href = "https://cdn.jsdelivr.net/npm/bulma@1.0.0/css/bulma.min.css")
        }
        body {
            section(classes = "section") {
                div(classes="container") {
                    block()
                }
            }
        }
    }.toString()

private fun FlowContent.showQuotes(quotes: IQuotes) {
    quotes.findAll()
        .map { fetchedQuotes ->
            fetchedQuotes.forEach { quote ->
                showQuote(quote)
            }
        }
}

private fun FlowContent.showQuote(quote: Quote) {
    p {
        p { +"-------${quote.at.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}-----------" }
        p {
            quote.lines.forEach { line ->
                +"${line.name}: ${line.text}"
            }
        }
        p { +"----------------------------------------" }
    }
}
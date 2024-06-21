package be.kunlabora.crafters.kunlaquota.web.ui

import be.kunlabora.crafters.kunlaquota.service.IQuotes
import kotlinx.html.body
import kotlinx.html.html
import kotlinx.html.p
import kotlinx.html.stream.appendHTML
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.servlet.function.RouterFunctionDsl
import org.springframework.web.servlet.function.ServerResponse
import java.io.StringWriter
import java.time.format.DateTimeFormatter

fun uiRoutes(quotes: IQuotes): RouterFunctionDsl.() -> Unit = {
    GET("") {
        ServerResponse.status(HttpStatus.OK)
            .contentType(MediaType.TEXT_HTML)
            .body(
                StringWriter().appendHTML().html {
                    body {
                        val fetchResult = quotes.findAll()
                        fetchResult
                            .map { quotes ->
                                quotes.forEach { quote ->
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
                            }
                    }
                }
                    .toString()
            )
    }
}
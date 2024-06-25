package be.kunlabora.crafters.kunlaquota.web.ui.components

import be.kunlabora.crafters.kunlaquota.service.IQuotes
import be.kunlabora.crafters.kunlaquota.service.domain.Quote
import be.kunlabora.crafters.kunlaquota.web.ui.components.Util.formatToKunlaDate
import kotlinx.html.FlowContent
import kotlinx.html.div
import kotlinx.html.p
import kotlinx.html.strong
import java.time.LocalDateTime

object ShowQuotesScreen {
    fun FlowContent.showQuotes(quotes: IQuotes) {
        quotes.findAll()
            .map { fetchedQuotes -> fetchedQuotes.forEach { quote(it) } }
    }

    private fun FlowContent.quote(quote: Quote) {
        div("box") {
            div("content") {
                quoteDate(quote.at)
                quoteLines(quote.lines)
            }
        }
    }

    private fun FlowContent.quoteLines(lines: List<Quote.Line>) {
        div("media") {
            div("media-content") {
                lines.forEach { quoteLine(it) }
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
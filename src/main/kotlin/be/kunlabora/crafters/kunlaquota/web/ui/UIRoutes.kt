package be.kunlabora.crafters.kunlaquota.web.ui

import be.kunlabora.crafters.kunlaquota.Failure
import be.kunlabora.crafters.kunlaquota.service.*
import be.kunlabora.crafters.kunlaquota.service.domain.Quote
import be.kunlabora.crafters.kunlaquota.service.domain.QuoteId
import be.kunlabora.crafters.kunlaquota.service.domain.QuoteShare
import be.kunlabora.crafters.kunlaquota.web.ui.screens.AddQuoteModal.addQuoteLine
import be.kunlabora.crafters.kunlaquota.web.ui.screens.AddQuoteModal.addQuoteModal
import be.kunlabora.crafters.kunlaquota.web.ui.screens.ShareQuoteModal.shareQuoteModal
import be.kunlabora.crafters.kunlaquota.web.ui.screens.ShowQuotesScreen.errorMessage
import be.kunlabora.crafters.kunlaquota.web.ui.screens.ShowQuotesScreen.showQuotes
import kotlinx.html.FlowContent
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.servlet.function.*

const val baseUiUrl = "/ui"

fun uiRoutes(quotes: IQuotes): RouterFunctionDsl.() -> Unit = {
    GET(RequestPredicates.param("share") { it.isNotBlank() }) { request ->
        val quoteShare = QuoteShare(request.paramOrNull("share")!!)
        val title = "Shared quote: ${quoteShare.value}"
        quotes.findByQuoteShare(quoteShare, SurroundingQuotesSize(3))
            .map { quotes ->
                wrapperResponse(title) { showQuotes(quotes) }
            }.andHandleFailure()
    }

    GET("") {
        val title = "KunlaQuota"
        wrapperResponse(title) {
            quotes.findAll().map { quotes -> showQuotes(quotes) }
        }
    }

    GET("showModal") {
        partialResponse { addQuoteModal() }
    }
    POST("new/addLine") {
        partialResponse { addQuoteLine() }
    }
    POST("new") { request ->
        val names = request.params()["nameInput"] ?: emptyList()
        val texts = request.params()["textInput"] ?: emptyList()

        val addQuote = AddQuote(
            names.zip(texts).mapIndexed { idx, (name, text) -> Quote.Line(idx, name, text) }
        )
        quotes.execute(addQuote)
            .map { hxRedirectResponse() }
            .andHandleFailure()
    }

    POST("share/{quoteId}") { request ->
        val quoteId: QuoteId = request.pathVariable("quoteId").let { QuoteId.fromString(it) }
        quotes.execute(ShareQuote(quoteId))
            .map { quoteShare ->
                val quoteShareUrl = quoteShare.toUrl(request)
                partialResponse { shareQuoteModal(quoteShareUrl) }
            }.andHandleFailure()
    }
}

private fun wrapperResponse(
    title: String,
    content: FlowContent.() -> Unit
) = ServerResponse
    .status(HttpStatus.OK)
    .contentType(MediaType.TEXT_HTML)
    .body(wrapper(title) { content() })

private fun partialResponse(content: FlowContent.() -> Unit) =
    ServerResponse
        .status(HttpStatus.OK)
        .contentType(MediaType.TEXT_HTML)
        .body(partial { content() })

private fun hxRedirectResponse() = ServerResponse
    .status(HttpStatus.OK)
    .header("HX-Redirect", baseUiUrl)
    .build()

private fun QuoteShare.toUrl(serverRequest: ServerRequest) =
    serverRequest.uriBuilder().replacePath(baseUiUrl).queryParam("share", value).build()

private fun Result<Failure, ServerResponse>.andHandleFailure() =
    recover { failure ->
        partialResponse { errorMessage("Oopsie! Something broke!", failure.message) }
    }.get()


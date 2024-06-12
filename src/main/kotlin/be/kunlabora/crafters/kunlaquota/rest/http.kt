package be.kunlabora.crafters.kunlaquota.rest

import be.kunlabora.crafters.kunlaquota.AddQuoteFailed
import be.kunlabora.crafters.kunlaquota.AddQuoteInvalid
import be.kunlabora.crafters.kunlaquota.ShareQuoteFailed
import be.kunlabora.crafters.kunlaquota.service.*
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.servlet.function.RouterFunctionDsl
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.body
import java.net.URI

fun apiRoutes(quotes: IQuotes): RouterFunctionDsl.() -> Unit = {
    fun AddQuote.execute() = quotes.execute(this)
    fun ShareQuote.execute() = quotes.execute(this)


    "/quote".nest {
        GET {
            quotes.findAll()
                .map { foundQuotes ->
                    ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(foundQuotes)
                }
                .recover { failure ->
                    ServerResponse.status(500).build().also { logger.error("$failure") }
                }.get()
        }
        POST("") { request ->
            request.body<AddQuote>()
                .execute()
                .map { quote ->
                    ServerResponse.created(quote.id.asUri(request)).build()
                }
                .recover { failure ->
                    when (failure) {
                        is AddQuoteInvalid -> ServerResponse.status(400).body(AddQuoteError(failure.message))
                            .also { logger.error(failure.message) }

                        is AddQuoteFailed -> ServerResponse.status(500).build().also { logger.error(failure.message) }
                    }
                }.get()
        }
        POST("share") { request ->
            request.body<ShareQuote>()
                .execute()
                .map { quoteShare ->
                    ServerResponse.ok().body(quoteShare.value)
                }
                .recover { failure ->
                    when (failure) {
                        is ShareQuoteFailed -> ServerResponse.status(500).build().also { logger.error(failure.message) }
                    }
                }.get()
        }
    }
}


private val logger = LoggerFactory.getLogger("KunlaQuotaLogger")

private fun <E> EntityId<E>.asUri(request: ServerRequest): URI =
    request.uriBuilder().path("/{id}").build(this.value)
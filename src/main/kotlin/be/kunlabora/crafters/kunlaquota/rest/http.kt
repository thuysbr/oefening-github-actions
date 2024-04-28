package be.kunlabora.crafters.kunlaquota.rest

import be.kunlabora.crafters.kunlaquota.AddQuoteFailed
import be.kunlabora.crafters.kunlaquota.AddQuoteInvalid
import be.kunlabora.crafters.kunlaquota.Failure
import be.kunlabora.crafters.kunlaquota.ShareQuoteFailed
import be.kunlabora.crafters.kunlaquota.service.*
import be.kunlabora.crafters.kunlaquota.service.domain.QuoteShare
import org.slf4j.LoggerFactory
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
                    ServerResponse.ok().body(foundQuotes)
                }
                .recover { failure ->
                    ServerResponse.status(500).build().also { logger.error("${failure}") }
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
        POST("{id}") { request ->
            request.body<ShareQuote>()
                .execute()
                .map { quoteShare ->
                    ServerResponse.created(quoteShare.asUri(request)).build()
                }
                .recover { failure ->
                    when (failure) {
                        is ShareQuoteFailed -> ServerResponse.status(500).build().also { logger.error(failure.message) }
                    }
                }.get()
        }
    }
}

private fun <T> Either<Failure, T>.handle(success: (Either.Right<T>) -> ServerResponse) =
    when (this) {
        is Either.Left -> ServerResponse.status(500).build().also { logger.error("$value") }
        is Either.Right -> success(this)
    }

private val logger = LoggerFactory.getLogger("KunlaQuotaLogger")

private fun <E> EntityId<E>.asUri(request: ServerRequest): URI =
    request.uriBuilder().path("/{id}").build(this.value)

private fun QuoteShare.asUri(request: ServerRequest): URI =
    request.uriBuilder().path("?share={id}").build(this.value)

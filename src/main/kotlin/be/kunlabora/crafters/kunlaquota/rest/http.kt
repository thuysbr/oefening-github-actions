package be.kunlabora.crafters.kunlaquota.rest

import org.springframework.web.servlet.function.RouterFunctionDsl
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.body
import java.net.URI
import java.util.*

val apiRoutes: RouterFunctionDsl.() -> Unit = {
    "/quote".nest {
        GET { ServerResponse.ok().body("Hello World") }
        POST { request ->
            val addQuote = request.body<AddQuote>()
            val quote: Quote = execute(addQuote)
            ServerResponse.created(quote.id.asUri(request)).build()
        }
    }
}

private fun <E> EntityId<E>.asUri(request: ServerRequest): URI =
    request.uriBuilder().path("/{id}").build(this.asString())

data class AddQuote(val quote: String)

fun execute(addQuote: AddQuote): Quote = Quote(QuoteId.new(), addQuote.quote)

data class Quote(val id: QuoteId, val quotedString: String)

typealias QuoteId = EntityId<Quote>

class EntityId<E> private constructor(private val value: UUID) {
    fun asString(): String = value.toString()
    companion object {
        fun <E> new(): EntityId<E> = EntityId(UUID.randomUUID())
        fun <E> fromString(value: String): EntityId<E> = EntityId(UUID.fromString(value))
    }
}
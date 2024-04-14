package be.kunlabora.crafters.kunlaquota.rest

import be.kunlabora.crafters.kunlaquota.service.AddQuote
import be.kunlabora.crafters.kunlaquota.service.EntityId
import be.kunlabora.crafters.kunlaquota.service.Quote
import be.kunlabora.crafters.kunlaquota.service.Quotes
import org.springframework.web.servlet.function.RouterFunctionDsl
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.body
import java.net.URI


fun apiRoutes(quotes: Quotes): RouterFunctionDsl.() -> Unit = {
    "/quote".nest {
        GET { ServerResponse.ok().body(quotes.findAll()) }
        POST { request ->
            val addQuote = request.body<AddQuote>()
            val quote: Quote = quotes.execute(addQuote)
            ServerResponse.created(quote.id.asUri(request)).build()
        }
    }
}

private fun <E> EntityId<E>.asUri(request: ServerRequest): URI =
    request.uriBuilder().path("/{id}").build(this.value)

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
    fun AddQuote.execute() = quotes.execute(this)
    fun Quote.toResponse(request: ServerRequest) = ServerResponse.created(id.asUri(request)).build()

    "/quote".nest {
        GET { ServerResponse.ok().body(quotes.findAll()) }
        POST { request ->
            request.body<AddQuote>()
                .execute()
                .toResponse(request)
        }
    }
}

private fun <E> EntityId<E>.asUri(request: ServerRequest): URI =
    request.uriBuilder().path("/{id}").build(this.value)

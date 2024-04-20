package be.kunlabora.crafters.kunlaquota.rest

import be.kunlabora.crafters.kunlaquota.Failure
import be.kunlabora.crafters.kunlaquota.service.Either
import be.kunlabora.crafters.kunlaquota.service.EntityId
import be.kunlabora.crafters.kunlaquota.service.Quotes
import be.kunlabora.crafters.kunlaquota.service.domain.AddQuote
import be.kunlabora.crafters.kunlaquota.service.domain.Quote
import org.springframework.web.servlet.function.RouterFunctionDsl
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.body
import java.net.URI


fun apiRoutes(quotes: Quotes): RouterFunctionDsl.() -> Unit = {
    fun AddQuote.execute() = quotes.execute(this)
    fun Either<Failure, Quote>.toResponse(request: ServerRequest) = when(this){
        is Either.Left -> TODO("error handling")
        is Either.Right -> ServerResponse.created(value.id.asUri(request)).build()
    }
    fun Either<Failure, List<Quote>>.toResponse() = when(this) {
        is Either.Left -> TODO("error handling")
        is Either.Right -> ServerResponse.ok().body(value)
    }

    "/quote".nest {
        GET {
            quotes.findAll()
                .toResponse()
        }
        POST { request ->
            request.body<AddQuote>()
                .execute()
                .toResponse(request)
        }
    }
}


private fun <E> EntityId<E>.asUri(request: ServerRequest): URI =
    request.uriBuilder().path("/{id}").build(this.value)

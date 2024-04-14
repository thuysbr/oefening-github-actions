package be.kunlabora.crafters.kunlaquota.rest

import be.kunlabora.crafters.kunlaquota.Failure
import be.kunlabora.crafters.kunlaquota.service.*
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

package be.kunlabora.crafters.kunlaquota.rest

import be.kunlabora.crafters.kunlaquota.Failure
import be.kunlabora.crafters.kunlaquota.service.Either
import be.kunlabora.crafters.kunlaquota.service.EntityId
import be.kunlabora.crafters.kunlaquota.service.IQuotes
import be.kunlabora.crafters.kunlaquota.service.domain.AddQuote
import org.slf4j.LoggerFactory
import org.springframework.web.servlet.function.RouterFunctionDsl
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.body
import java.net.URI

fun apiRoutes(quotes: IQuotes): RouterFunctionDsl.() -> Unit = {
    fun AddQuote.execute() = quotes.execute(this)

    "/quote".nest {
        GET {
            quotes.findAll()
                .handle { success ->
                    ServerResponse.ok().body(success.value)
                }
        }
        POST { request ->
            request.body<AddQuote>()
                .execute()
                .handle { success ->
                    ServerResponse.created(success.value.id.asUri(request)).build()
                }
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

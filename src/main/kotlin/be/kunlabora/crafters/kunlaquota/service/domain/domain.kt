package be.kunlabora.crafters.kunlaquota.service.domain

import be.kunlabora.crafters.kunlaquota.Failure
import be.kunlabora.crafters.kunlaquota.service.Command
import be.kunlabora.crafters.kunlaquota.service.Either
import be.kunlabora.crafters.kunlaquota.service.EntityId

data class AddQuote(val name: String, val text: String): Command

typealias QuoteId = EntityId<Quote>

data class Quote(
    val id: QuoteId,
    val name: String,
    val text: String
)

interface QuoteRepository {
    fun store(quote: Quote): Either<Failure, Quote>
    fun findAll(): Either<Failure, List<Quote>>
}
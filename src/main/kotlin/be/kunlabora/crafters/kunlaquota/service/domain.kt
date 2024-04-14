package be.kunlabora.crafters.kunlaquota.service

import be.kunlabora.crafters.kunlaquota.Failure

data class AddQuote(val name: String, val text: String): Command

typealias QuoteId = EntityId<Quote>

data class Quote(
    val id: QuoteId,
    val name: String,
    val text: String
)

interface QuoteRepository {
    fun store(quote: Quote): Either<Failure, Quote>
    fun findAll(): List<Quote>
}
package be.kunlabora.crafters.kunlaquota.service.domain

import be.kunlabora.crafters.kunlaquota.Failure
import be.kunlabora.crafters.kunlaquota.service.Command
import be.kunlabora.crafters.kunlaquota.service.Either
import be.kunlabora.crafters.kunlaquota.service.EntityId

data class AddQuote(
    val lines: List<Quote.Line>
): Command

typealias QuoteId = EntityId<Quote>

data class Quote(
    val id: QuoteId,
    val lines: List<Line> = emptyList(),
) {
    data class Line(
        val order: Int,
        val name: String,
        val text: String,
    )
}

interface QuoteRepository {
    fun store(quote: Quote): Either<Failure, Quote>
    fun findAll(): Either<Failure, List<Quote>>
}
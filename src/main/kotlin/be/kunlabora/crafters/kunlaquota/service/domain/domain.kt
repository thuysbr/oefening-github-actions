package be.kunlabora.crafters.kunlaquota.service.domain

import be.kunlabora.crafters.kunlaquota.AddFailure
import be.kunlabora.crafters.kunlaquota.FetchQuotesFailed
import be.kunlabora.crafters.kunlaquota.service.Either
import be.kunlabora.crafters.kunlaquota.service.EntityId
import java.time.LocalDateTime

typealias QuoteId = EntityId<Quote>

data class Quote(
    val id: QuoteId,
    val lines: List<Line> = emptyList(),
    val at: LocalDateTime = LocalDateTime.now(),
) {
    data class Line(
        val order: Int,
        val name: String,
        val text: String,
    )
}

interface QuoteRepository {
    fun store(quote: Quote): Either<AddFailure, Quote>
    fun findAll(): Either<FetchQuotesFailed, List<Quote>>
}

typealias QuoteShareProvider = (QuoteId) -> QuoteShare

@JvmInline value class QuoteShare(val value: String)
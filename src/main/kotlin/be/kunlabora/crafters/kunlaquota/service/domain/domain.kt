package be.kunlabora.crafters.kunlaquota.service.domain

import be.kunlabora.crafters.kunlaquota.AddFailure
import be.kunlabora.crafters.kunlaquota.FetchQuotesFailed
import be.kunlabora.crafters.kunlaquota.service.EntityId
import be.kunlabora.crafters.kunlaquota.service.Result
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
    fun store(quote: Quote): Result<AddFailure, Quote>
    fun findAll(): Result<FetchQuotesFailed, List<Quote>>
}

interface CanShareQuotes {
    operator fun invoke(quoteId: QuoteId): QuoteShare
}

@JvmInline value class QuoteShare(val value: String)
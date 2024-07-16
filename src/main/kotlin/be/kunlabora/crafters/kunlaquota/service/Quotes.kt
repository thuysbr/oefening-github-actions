package be.kunlabora.crafters.kunlaquota.service

import be.kunlabora.crafters.kunlaquota.AddFailure
import be.kunlabora.crafters.kunlaquota.FetchQuotesFailed
import be.kunlabora.crafters.kunlaquota.ShareFailure
import be.kunlabora.crafters.kunlaquota.service.Result.Error
import be.kunlabora.crafters.kunlaquota.service.Result.Ok
import be.kunlabora.crafters.kunlaquota.service.domain.*
import java.time.LocalDateTime

interface IQuotes {
    fun execute(
        addQuote: AddQuote,
        dateProvider: () -> LocalDateTime = { LocalDateTime.now() }
    ): Result<AddFailure, Quote>

    fun execute(shareQuote: ShareQuote): Result<ShareFailure, QuoteShare>
    fun findAll(): Result<FetchQuotesFailed, List<Quote>>
    fun findByQuoteShare(quoteShare: QuoteShare, surroundingQuotesSize: SurroundingQuotesSize): Result<FetchQuotesFailed, List<Quote>>
}

class Quotes(
    private val quoteRepository: QuoteRepository,
    private val quoteShareProvider: QuoteShareProvider,
    private val quoteShareRepository: QuoteShareRepository,
) : IQuotes {

    override fun execute(addQuote: AddQuote, dateProvider: () -> LocalDateTime): Result<AddFailure, Quote> =
        addQuote
            .validate()
            .flatMap { validatedAddQuote ->
                Quote(id = QuoteId.new(), at = dateProvider(), lines = validatedAddQuote.lines).store()
            }

    override fun execute(shareQuote: ShareQuote): Result<ShareFailure, QuoteShare> =
        quoteShareRepository.upsert(shareQuote.id, quoteShareProvider)


    private fun Quote.store() = quoteRepository.store(this)

    override fun findAll(): Result<FetchQuotesFailed, List<Quote>> = quoteRepository.findAll()

    override fun findByQuoteShare(
        quoteShare: QuoteShare,
        surroundingQuotesSize: SurroundingQuotesSize
    ): Result<FetchQuotesFailed, List<Quote>> {
        return when (val quoteShareResult = quoteShareRepository.findBy(quoteShare)) {
            is Ok -> quoteRepository.findAll().map { quotes ->
                quotes.fetchSurroundingQuotes(quoteShareResult.value, surroundingQuotesSize)
            }
            is Error -> Ok(emptyList())
        }
    }

    private fun List<Quote>.fetchSurroundingQuotes(
        foundQuoteId: QuoteId?,
        surroundingQuotesSize: SurroundingQuotesSize
    ): List<Quote> {
        val foundQuoteIdx = indexOfFirst { it.id == foundQuoteId }
        val surroundingIndexes = surroundingQuotesSize.surroundingIndexes(foundQuoteIdx)
        return when (foundQuoteIdx) {
            -1 -> emptyList()
            0 -> take(surroundingQuotesSize.value)
            size - 1 -> takeLast(surroundingQuotesSize.value)
            else -> surroundingIndexes.mapNotNull { this.getOrNull(it) }.let {
                if (it.size == surroundingQuotesSize.value) it
                else it.fetchSurroundingQuotes(foundQuoteId, surroundingQuotesSize.nextLowerSurroundingSize())
            }
        }
    }
}

@JvmInline
value class SurroundingQuotesSize(val value: Int) {
    init {
        require(value % 2 == 1) { "SurroundingQuotesSize should always be odd." }
        require(value > 0) { "SurroundingQuotesSize should be strictly more than 0." }
    }

    fun nextLowerSurroundingSize() = SurroundingQuotesSize((this.value / 2) + 1)
    fun surroundingIndexes(idx: Int): List<Int> =
        (this.value / 2).let { maxExtent ->
            (maxExtent.negated()..maxExtent).map { idx + it }
        }
    private fun Int.negated() = (this * -1)
}

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
    fun findByQuoteShare(quoteShare: QuoteShare): Result<FetchQuotesFailed, List<Quote>>
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

    override fun findByQuoteShare(quoteShare: QuoteShare): Result<FetchQuotesFailed, List<Quote>> {
        return when (val quoteShareResult = quoteShareRepository.findBy(quoteShare)) {
            is Ok -> quoteRepository.findAll().map { quotes -> fetchSurroundingQuotes(quotes, quoteShareResult.value) }
            is Error -> Ok(emptyList())
        }
    }

    private fun fetchSurroundingQuotes(
        quotes: List<Quote>,
        foundQuoteId: QuoteId?
    ): List<Quote> {
        val foundQuoteIdx = quotes.indexOfFirst { it.id == foundQuoteId }
        return when (foundQuoteIdx) {
            -1 -> emptyList()
            0 -> quotes.take(3)
            quotes.size -1 -> quotes.takeLast(3)
            else -> listOf(quotes[foundQuoteIdx-1], quotes[foundQuoteIdx], quotes[foundQuoteIdx+1])
        }
    }
}

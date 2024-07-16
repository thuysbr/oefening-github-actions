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
        val foundQuoteId = when (val quoteShareResult = quoteShareRepository.findBy(quoteShare)) {
            is Ok -> quoteShareResult.value
            is Error -> null
        }
        return quoteRepository.findAll().map { quotes ->
            if (foundQuoteId !in quotes.map { it.id }) emptyList()
            else fetchSurroundingQuotes(quotes, foundQuoteId)
        }
    }

    private fun fetchSurroundingQuotes(
        quotes: List<Quote>,
        foundQuoteId: QuoteId?
    ): List<Quote> {
        val foundQuoteIdx = quotes.indexOfFirst { it.id == foundQuoteId }
        return when {
            foundQuoteIdx == 0 -> quotes.take(3)
            foundQuoteIdx == quotes.size -1 -> quotes.takeLast(3)
            foundQuoteIdx > 0 && foundQuoteIdx < quotes.size -1 -> listOf(quotes[foundQuoteIdx-1], quotes[foundQuoteIdx], quotes[foundQuoteIdx+1])
            else -> error("stuff blew up")
        }
    }
}

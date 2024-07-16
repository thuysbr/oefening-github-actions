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
            else { //the else block is the main thing, but it's nested
                val foundQuoteIdx = quotes.indexOfFirst { it.id == foundQuoteId }
                //feels like there's some kind of symmetry
                val lastIdx = if (foundQuoteIdx + 1 > quotes.size - 1) foundQuoteIdx else foundQuoteIdx + 1
                val firstIdx = if (foundQuoteIdx - 1 < 0) foundQuoteIdx else foundQuoteIdx - 1

                val firstQuote = quotes[firstIdx]
                val sharedQuote = quotes[foundQuoteIdx]
                val lastQuote = quotes[lastIdx]

                val option = setOf(firstQuote, sharedQuote, lastQuote).toList()
                val result = if (option.size == 3) option
                else if (option[0].id == sharedQuote.id) option + quotes.getOrNull(foundQuoteIdx + 2)
                else if (option[1].id == sharedQuote.id) option + quotes.getOrNull(foundQuoteIdx - 2)
                else emptyList() //sign that conditional is not exhaustive, because we shouldn't ever be in this case
                result.filterNotNull().sortedByDescending { it.at } //we have to sort again
            }
        }
    }
}

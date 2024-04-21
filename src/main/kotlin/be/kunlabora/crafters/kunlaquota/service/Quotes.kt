package be.kunlabora.crafters.kunlaquota.service

import be.kunlabora.crafters.kunlaquota.Failure
import be.kunlabora.crafters.kunlaquota.service.domain.AddQuote
import be.kunlabora.crafters.kunlaquota.service.domain.Quote
import be.kunlabora.crafters.kunlaquota.service.domain.QuoteId
import be.kunlabora.crafters.kunlaquota.service.domain.QuoteRepository

interface IQuotes {
    fun execute(addQuote: AddQuote): Either<Failure, Quote>
    fun findAll() : Either<Failure, List<Quote>>
}

class Quotes(
    private val quoteRepository: QuoteRepository,
) : IQuotes {
    override fun execute(addQuote: AddQuote): Either<Failure, Quote> =
        Quote(QuoteId.new(), addQuote.lines)
            .store()

    private fun Quote.store() = quoteRepository.store(this)

    override fun findAll() : Either<Failure, List<Quote>> = quoteRepository.findAll()
}

package be.kunlabora.crafters.kunlaquota.service

import be.kunlabora.crafters.kunlaquota.Failure
import be.kunlabora.crafters.kunlaquota.service.domain.AddQuote
import be.kunlabora.crafters.kunlaquota.service.domain.Quote
import be.kunlabora.crafters.kunlaquota.service.domain.QuoteId
import be.kunlabora.crafters.kunlaquota.service.domain.QuoteRepository

class Quotes(
    private val quoteRepository: QuoteRepository,
) {
    fun execute(addQuote: AddQuote): Either<Failure, Quote> =
        Quote(QuoteId.new(), "Snarf", addQuote.text)
            .store()

    private fun Quote.store() = quoteRepository.store(this)

    fun findAll() : Either<Failure, List<Quote>> = quoteRepository.findAll()
}

package be.kunlabora.crafters.kunlaquota.service

import be.kunlabora.crafters.kunlaquota.Failure

class Quotes(
    private val quoteRepository: QuoteRepository,
) {
    fun execute(addQuote: AddQuote): Either<Failure, Quote> =
        Quote(QuoteId.new(), "Snarf", addQuote.text)
            .store()

    private fun Quote.store() = quoteRepository.store(this)

    fun findAll() : Either<Failure, List<Quote>> = quoteRepository.findAll()
}

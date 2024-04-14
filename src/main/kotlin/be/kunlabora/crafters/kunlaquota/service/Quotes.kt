package be.kunlabora.crafters.kunlaquota.service

class Quotes(
    private val quoteRepository: QuoteRepository,
) {
    fun execute(addQuote: AddQuote): Quote =
        Quote(QuoteId.new(), addQuote.quote)
            .store()

    private fun Quote.store() = quoteRepository.store(this)

    fun findAll() = quoteRepository.findAll()
}

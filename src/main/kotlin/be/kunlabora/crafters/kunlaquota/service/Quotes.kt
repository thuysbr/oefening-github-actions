package be.kunlabora.crafters.kunlaquota.service

class Quotes(
    private val quoteRepository: QuoteRepository,
) {
    fun execute(addQuote: AddQuote): Quote =
        Quote(QuoteId.new(), addQuote.quote)
            .also { store(it) }

    private fun store(quote: Quote) = quoteRepository.store(quote)

    fun findAll() = quoteRepository.findAll()
}

data class AddQuote(val quote: String)

typealias QuoteId = EntityId<Quote>

data class Quote(
    val id: QuoteId,
    val text: String
)

interface QuoteRepository {
    fun store(quote: Quote): Quote
    fun findAll(): List<Quote>
}
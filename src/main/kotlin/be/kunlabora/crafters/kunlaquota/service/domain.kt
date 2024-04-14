package be.kunlabora.crafters.kunlaquota.service

data class AddQuote(val quote: String): Command

typealias QuoteId = EntityId<Quote>

data class Quote(
    val id: QuoteId,
    val text: String
)

interface QuoteRepository {
    fun store(quote: Quote): Quote
    fun findAll(): List<Quote>
}
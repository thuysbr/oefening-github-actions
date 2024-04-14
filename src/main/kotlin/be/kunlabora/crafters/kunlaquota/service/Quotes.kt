package be.kunlabora.crafters.kunlaquota.service

import java.util.*

interface QuoteRepository {
    fun store(quote: Quote): Quote
    fun findAll(): List<Quote>
}

class QuoteRepositoryStub : QuoteRepository {
    private val backingList: MutableList<Quote> = mutableListOf()

    override fun store(quote: Quote) = quote.also { backingList.add(quote) }
    override fun findAll() = backingList.toList()
}

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
data class Quote(val id: QuoteId, val quotedString: String)


@Suppress("unused")
class EntityId<E> private constructor(val value: String) {
    companion object {
        fun <E> new(): EntityId<E> = EntityId(UUID.randomUUID().toString())
    }
}
package be.kunlabora.crafters.kunlaquota.service

import java.util.*

class Quotes {
    private val backingList: MutableList<Quote> = mutableListOf()

    fun execute(addQuote: AddQuote): Quote =
        Quote(QuoteId.new(), addQuote.quote)
            .also { store(it) }
    private fun store(quote: Quote) = backingList.add(quote)

    fun findAll() = backingList.toList()
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
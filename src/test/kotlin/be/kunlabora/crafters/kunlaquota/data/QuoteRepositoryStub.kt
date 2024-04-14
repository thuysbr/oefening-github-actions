package be.kunlabora.crafters.kunlaquota.data

import be.kunlabora.crafters.kunlaquota.service.Quote
import be.kunlabora.crafters.kunlaquota.service.QuoteRepository

class QuoteRepositoryStub : QuoteRepository {
    private val backingList: MutableList<Quote> = mutableListOf()

    override fun store(quote: Quote) = quote.also { backingList.add(quote) }
    override fun findAll() = backingList.toList()
}
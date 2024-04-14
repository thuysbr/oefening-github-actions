package be.kunlabora.crafters.kunlaquota.data

import be.kunlabora.crafters.kunlaquota.service.Quote
import be.kunlabora.crafters.kunlaquota.service.QuoteRepository

class InMemQuoteRepository(): QuoteRepository {
    override fun store(quote: Quote): Quote {
        TODO("Not yet implemented")
    }

    override fun findAll(): List<Quote> {
        TODO("Not yet implemented")
    }

}
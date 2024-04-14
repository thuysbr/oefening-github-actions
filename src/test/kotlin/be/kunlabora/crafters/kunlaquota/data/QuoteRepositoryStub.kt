package be.kunlabora.crafters.kunlaquota.data

import be.kunlabora.crafters.kunlaquota.CanReturnFailure
import be.kunlabora.crafters.kunlaquota.Failure
import be.kunlabora.crafters.kunlaquota.FailureStub
import be.kunlabora.crafters.kunlaquota.service.Either
import be.kunlabora.crafters.kunlaquota.service.Quote
import be.kunlabora.crafters.kunlaquota.service.QuoteRepository

class QuoteRepositoryStub(private val orFail: FailureStub = FailureStub()) : QuoteRepository, CanReturnFailure by orFail {
    private val backingList: MutableList<Quote> = mutableListOf()

    override fun store(quote: Quote): Either<Failure, Quote> = orFail(QuoteRepository::store.name) {
        quote.also { backingList.add(quote) }
    }
    override fun findAll() : Either<Failure, List<Quote>> = orFail(QuoteRepository::findAll.name) {
        backingList.toList()
    }
}
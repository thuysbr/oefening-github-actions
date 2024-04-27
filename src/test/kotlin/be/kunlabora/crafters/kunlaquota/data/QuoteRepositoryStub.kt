package be.kunlabora.crafters.kunlaquota.data

import be.kunlabora.crafters.kunlaquota.*
import be.kunlabora.crafters.kunlaquota.service.Either
import be.kunlabora.crafters.kunlaquota.service.Either.Left
import be.kunlabora.crafters.kunlaquota.service.Either.Right
import be.kunlabora.crafters.kunlaquota.service.domain.Quote
import be.kunlabora.crafters.kunlaquota.service.domain.QuoteRepository

class QuoteRepositoryStub(private val orFail: FailureStub<Failure> = FailureStub()) : QuoteRepository, CanReturnFailure<Failure> by orFail {
    private val backingList: MutableList<Quote> = mutableListOf()

    override fun store(quote: Quote): Either<AddFailure, Quote> = orFail(QuoteRepository::store.name) {
        if (quote.id in backingList.map { it.id }) Left(AddQuoteFailed("Quote already exists!"))
        else Right(quote).also { backingList.add(quote) }
    } as Either<AddFailure, Quote>

    override fun findAll() : Either<FetchQuotesFailed, List<Quote>> = orFail(QuoteRepository::findAll.name) {
        Right(backingList.toList())
    } as Either<FetchQuotesFailed, List<Quote>>
}
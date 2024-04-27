package be.kunlabora.crafters.kunlaquota.service

import be.kunlabora.crafters.kunlaquota.AddFailure
import be.kunlabora.crafters.kunlaquota.AddQuoteInvalid
import be.kunlabora.crafters.kunlaquota.FetchQuotesFailed
import be.kunlabora.crafters.kunlaquota.service.Either.Left
import be.kunlabora.crafters.kunlaquota.service.Either.Right
import be.kunlabora.crafters.kunlaquota.service.domain.AddQuote
import be.kunlabora.crafters.kunlaquota.service.domain.Quote
import be.kunlabora.crafters.kunlaquota.service.domain.QuoteId
import be.kunlabora.crafters.kunlaquota.service.domain.QuoteRepository

interface IQuotes {
    fun execute(addQuote: AddQuote): Either<AddFailure, Quote>
    fun findAll(): Either<FetchQuotesFailed, List<Quote>>
}

class Quotes(
    private val quoteRepository: QuoteRepository,
) : IQuotes {
    override fun execute(addQuote: AddQuote): Either<AddFailure, Quote> =
        addQuote
            .validate()
            .flatMap { validatedAddQuote ->
                Quote(QuoteId.new(), validatedAddQuote.lines)
                    .store()
            }


    private fun Quote.store() = quoteRepository.store(this)

    override fun findAll(): Either<FetchQuotesFailed, List<Quote>> = quoteRepository.findAll()

    private fun AddQuote.validate(): Either<AddFailure, AddQuote> =
        if (someLinesHaveSameOrder()) Left(AddQuoteInvalid("Can't have multiple lines with the same order."))
        else Right(this)

    private fun AddQuote.someLinesHaveSameOrder() =
        this.lines.map { it.order }.toSet().size < this.lines.size
}

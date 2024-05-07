package be.kunlabora.crafters.kunlaquota.service

import be.kunlabora.crafters.kunlaquota.AddFailure
import be.kunlabora.crafters.kunlaquota.AddQuoteInvalid
import be.kunlabora.crafters.kunlaquota.FetchQuotesFailed
import be.kunlabora.crafters.kunlaquota.ShareFailure
import be.kunlabora.crafters.kunlaquota.service.Either.Left
import be.kunlabora.crafters.kunlaquota.service.Either.Right
import be.kunlabora.crafters.kunlaquota.service.domain.*
import java.time.LocalDateTime

interface IQuotes {
    fun execute(addQuote: AddQuote, dateProvider: () -> LocalDateTime = { LocalDateTime.now() }): Either<AddFailure, Quote>
    fun execute(shareQuote: ShareQuote): Either<ShareFailure, QuoteShare>
    fun findAll(): Either<FetchQuotesFailed, List<Quote>>
}

class Quotes(
    private val quoteRepository: QuoteRepository,
    private val quoteShareProvider: QuoteShareProvider,
) : IQuotes {
    override fun execute(addQuote: AddQuote, dateProvider : () -> LocalDateTime): Either<AddFailure, Quote> =
        addQuote
            .validate()
            .flatMap { validatedAddQuote ->
                Quote(id = QuoteId.new(), at = dateProvider(), lines = validatedAddQuote.lines).store()
            }

    override fun execute(shareQuote: ShareQuote): Either<ShareFailure, QuoteShare> {
        return Right(quoteShareProvider(shareQuote.id))
    }


    private fun Quote.store() = quoteRepository.store(this)

    override fun findAll(): Either<FetchQuotesFailed, List<Quote>> = quoteRepository.findAll()

    private fun AddQuote.validate(): Either<AddFailure, AddQuote> =
        if (someLinesHaveSameOrder()) Left(AddQuoteInvalid("Can't have multiple lines with the same order."))
        else Right(this)

    private fun AddQuote.someLinesHaveSameOrder() =
        this.lines.map { it.order }.toSet().size < this.lines.size
}

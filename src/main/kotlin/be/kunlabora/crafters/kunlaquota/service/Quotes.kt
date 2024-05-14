package be.kunlabora.crafters.kunlaquota.service

import be.kunlabora.crafters.kunlaquota.AddFailure
import be.kunlabora.crafters.kunlaquota.AddQuoteInvalid
import be.kunlabora.crafters.kunlaquota.FetchQuotesFailed
import be.kunlabora.crafters.kunlaquota.ShareFailure
import be.kunlabora.crafters.kunlaquota.service.Result.Error
import be.kunlabora.crafters.kunlaquota.service.Result.Ok
import be.kunlabora.crafters.kunlaquota.service.domain.*
import java.time.LocalDateTime

interface IQuotes {
    fun execute(
        addQuote: AddQuote,
        dateProvider: () -> LocalDateTime = { LocalDateTime.now() }
    ): Result<AddFailure, Quote>

    fun execute(shareQuote: ShareQuote): Result<ShareFailure, QuoteShare>
    fun findAll(): Result<FetchQuotesFailed, List<Quote>>
}

class Quotes(
    private val quoteRepository: QuoteRepository,
    private val quoteShareProvider: QuoteShareProvider,
    private val quoteShareRepository: QuoteShareRepository = QuoteShareRepositoryFake(),
) : IQuotes {

    override fun execute(addQuote: AddQuote, dateProvider: () -> LocalDateTime): Result<AddFailure, Quote> =
        addQuote
            .validate()
            .flatMap { validatedAddQuote ->
                Quote(id = QuoteId.new(), at = dateProvider(), lines = validatedAddQuote.lines).store()
            }

    override fun execute(shareQuote: ShareQuote): Result<ShareFailure, QuoteShare> =
        quoteShareRepository.upsert(shareQuote.id, quoteShareProvider)


    private fun Quote.store() = quoteRepository.store(this)

    override fun findAll(): Result<FetchQuotesFailed, List<Quote>> = quoteRepository.findAll()

    private fun AddQuote.validate(): Result<AddFailure, AddQuote> =
        if (someLinesHaveSameOrder()) Error(AddQuoteInvalid("Can't have multiple lines with the same order."))
        else Ok(this)

    private fun AddQuote.someLinesHaveSameOrder() =
        this.lines.map { it.order }.toSet().size < this.lines.size
}

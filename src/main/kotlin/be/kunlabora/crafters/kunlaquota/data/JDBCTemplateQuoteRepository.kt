package be.kunlabora.crafters.kunlaquota.data

import be.kunlabora.crafters.kunlaquota.AddFailure
import be.kunlabora.crafters.kunlaquota.FetchQuotesFailed
import be.kunlabora.crafters.kunlaquota.service.Result
import be.kunlabora.crafters.kunlaquota.service.domain.Quote
import be.kunlabora.crafters.kunlaquota.service.domain.QuoteRepository
import java.time.LocalDateTime

class JDBCTemplateQuoteRepository(
    private val quoteDAO: QuoteDAO,
) : QuoteRepository {

    override fun store(quote: Quote): Result<AddFailure, Quote> = TODO()

    override fun findAll(): Result<FetchQuotesFailed, List<Quote>> = TODO()

}

data class QuoteRecordJdbc(
    val id: String,
    val at: LocalDateTime,
    val lines: Set<QuoteLineRecord>,
)

data class QuoteLineRecordJdbc(
    val order: Int,
    val name: String,
    val text: String,
)

package be.kunlabora.crafters.kunlaquota.data

import be.kunlabora.crafters.kunlaquota.AddQuoteFailed
import be.kunlabora.crafters.kunlaquota.FetchQuotesFailed
import be.kunlabora.crafters.kunlaquota.service.Either
import be.kunlabora.crafters.kunlaquota.service.Either.Left
import be.kunlabora.crafters.kunlaquota.service.Either.Right
import be.kunlabora.crafters.kunlaquota.service.domain.Quote
import be.kunlabora.crafters.kunlaquota.service.domain.QuoteId
import be.kunlabora.crafters.kunlaquota.service.domain.QuoteRepository
import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository

class DBQuoteRepository(
    private val jdbcAggregateTemplate: JdbcAggregateTemplate,
    private val quoteDAO: QuoteDAO,
) : QuoteRepository {

    /*
     * JdbcAggregateTemplate required, because we decide in code when an entity gets a new EntityId
     * Using the DAO would delegate to an update method instead of an insert method ¯\_(ツ)_/¯
     */
    override fun store(quote: Quote): Either<AddQuoteFailed, Quote> =
        if (quoteDAO.existsById(quote.id.value)) Left(AddQuoteFailed("Quote already exists!"))
        else Right(jdbcAggregateTemplate.insert(quote.toRecord()).toQuote())

    override fun findAll(): Either<FetchQuotesFailed, List<Quote>> =
        Right(quoteDAO.findAll()
            .map { quoteRecord -> quoteRecord.toQuote() })

    private fun Quote.toRecord() =
        QuoteRecord(id.value, name, text)

    private fun QuoteRecord.toQuote() =
        Quote(QuoteId.fromString(id), name, text)

}

@Table("quote")
data class QuoteRecord(
    @Id
    val id: String,
    val name: String,
    val text: String,
)

interface QuoteDAO : CrudRepository<QuoteRecord, String>
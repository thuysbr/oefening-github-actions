package be.kunlabora.crafters.kunlaquota.data.jpa

import be.kunlabora.crafters.kunlaquota.AddFailure
import be.kunlabora.crafters.kunlaquota.AddQuoteFailed
import be.kunlabora.crafters.kunlaquota.FetchQuotesFailed
import be.kunlabora.crafters.kunlaquota.service.Result
import be.kunlabora.crafters.kunlaquota.service.Result.Error
import be.kunlabora.crafters.kunlaquota.service.Result.Ok
import be.kunlabora.crafters.kunlaquota.service.domain.Quote
import be.kunlabora.crafters.kunlaquota.service.domain.QuoteId
import be.kunlabora.crafters.kunlaquota.service.domain.QuoteRepository
import jakarta.persistence.*
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.time.LocalDateTime

class DBJPAQuoteRepository(
    private val quoteDAO: QuoteDAOJPA,
) : QuoteRepository {

    override fun store(quote: Quote): Result<AddFailure, Quote> =
        if (quoteDAO.existsById(quote.id.value)) {
            Error(AddQuoteFailed("Quote already exists!"))
        } else {
            //TODO try to clean up now that we no longer have bidirectionality
            val quoteRecord = quote.toPartialRecord()
            val quoteLines = quote.lines.map { it.toRecord(quoteRecord) }.toSet()
            val completeQuoteRecord = quoteRecord.apply { lines = quoteLines }
            Ok(quoteDAO.save(completeQuoteRecord).toQuote())
        }

    override fun findAll(): Result<FetchQuotesFailed, List<Quote>> =
        Ok(quoteDAO.findAllSortedDesc().map { it.toQuote() })

    private fun Quote.toPartialRecord() = QuoteRecordJPA(id = id.value, at = at)

    private fun Quote.Line.toRecord(quoteRecord: QuoteRecordJPA) =
        QuoteLineRecordJPA(QuoteLineId(quoteRecord.id, order), name, text)

    private fun QuoteRecordJPA.toQuote() =
        Quote(
            id = QuoteId.fromString(id),
            at = at,
            lines = lines.map { line -> line.toQuoteLine() }.sortedBy { it.order })

    private fun QuoteLineRecordJPA.toQuoteLine() =
        Quote.Line(id.order, name, text)

}

@Entity
@Table(name = "quote")
data class QuoteRecordJPA(
    @Id
    val id: String,
    val at: LocalDateTime,
) {
    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "quote")
    lateinit var lines: Set<QuoteLineRecordJPA>
}

@Entity
@Table(name = "quote_lines")
data class QuoteLineRecordJPA(
    @EmbeddedId
    val id: QuoteLineId,
    val name: String,
    val text: String,
)

@Embeddable
data class QuoteLineId(
    @Column(name = "quote")
    val quote: String,

    @Column(name = "\"order\"")
    val order: Int
)

interface QuoteDAOJPA : CrudRepository<QuoteRecordJPA, String> {

    @Query("select q from QuoteRecordJPA q order by q.at desc")
    fun findAllSortedDesc(): List<QuoteRecordJPA>
}
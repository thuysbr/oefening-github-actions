package be.kunlabora.crafters.kunlaquota.data.jdbc

import be.kunlabora.crafters.kunlaquota.AddFailure
import be.kunlabora.crafters.kunlaquota.AddQuoteFailed
import be.kunlabora.crafters.kunlaquota.FetchQuotesFailed
import be.kunlabora.crafters.kunlaquota.service.Result
import be.kunlabora.crafters.kunlaquota.service.Result.Error
import be.kunlabora.crafters.kunlaquota.service.Result.Ok
import be.kunlabora.crafters.kunlaquota.service.domain.Quote
import be.kunlabora.crafters.kunlaquota.service.domain.QuoteId
import be.kunlabora.crafters.kunlaquota.service.domain.QuoteRepository
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.ResultSet
import java.time.LocalDateTime

class JDBCTemplateQuoteRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) : QuoteRepository {

    override fun store(quote: Quote): Result<AddFailure, Quote> =
        if (quote.exists()) Error(AddQuoteFailed("Quote already exists!"))
        else Ok(quote.also { insert(it) })

    override fun findAll(): Result<FetchQuotesFailed, List<Quote>> =
        fetchAllRowsOrderedByAt()
            .convertToQuotes()
            .asOk()

    private fun MutableList<QuoteRow>.convertToQuotes(): List<Quote> =
        groupBy(QuoteRow::quoteId).map(::toQuote)

    private fun toQuote(entry: Map.Entry<String, List<QuoteRow>>): Quote =
        entry.let { (quoteId, rows) ->
            Quote(
                id = QuoteId.fromString(quoteId),
                at = rows.first().at,
                lines = rows.map { Quote.Line(it.order, it.name, it.text) }
            )
        }

    private fun List<Quote>.asOk() = Ok(this)

    private fun fetchAllRowsOrderedByAt(): MutableList<QuoteRow> =
        jdbcTemplate.query(
            """
                select q.id as quoteId, q.at as at, ql.order as lineOrder, ql.name as lineName, ql.text as lineText 
                from quote q
                join quote_lines ql on q.id = ql.quote
                order by q.at desc
                """.trimIndent(),
            QuoteRowMapper
        )

    private fun insert(quote: Quote) {
        jdbcTemplate.update(
            """
            INSERT INTO QUOTE (ID, AT)
            VALUES (:id, :at)
            """.trimIndent(),
            mapOf(
                "id" to quote.id.value,
                "at" to quote.at,
            )
        )
        quote.lines.forEach { quoteLine ->
            jdbcTemplate.update(
                """
                INSERT INTO quote_lines (quote, "order", name, text)
                VALUES (:quoteId, :order, :name, :text)
                """.trimIndent(),
                mapOf(
                    "quoteId" to quote.id.value,
                    "order" to quoteLine.order,
                    "name" to quoteLine.name,
                    "text" to quoteLine.text,
                )
            )
        }
    }

    private fun Quote.exists() = jdbcTemplate.queryForObject(
        """select count(*) from quote where id = :id""",
        mapOf("id" to id.value),
        Int::class.java
    )!! > 0
}

object QuoteRowMapper : RowMapper<QuoteRow> {
    override fun mapRow(rs: ResultSet, rowNum: Int): QuoteRow =
        QuoteRow(
            quoteId = rs.getString("quoteId"),
            at = rs.getTimestamp("at").toLocalDateTime(),
            order = rs.getInt("lineOrder"),
            name = rs.getString("lineName"),
            text = rs.getString("lineText"),
        )
}

data class QuoteRow(
    val quoteId: String,
    val at: LocalDateTime,
    val order: Int,
    val name: String,
    val text: String,
)
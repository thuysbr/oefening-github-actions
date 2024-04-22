package be.kunlabora.crafters.kunlaquota.service

import be.kunlabora.crafters.kunlaquota.AddQuoteFailed
import be.kunlabora.crafters.kunlaquota.FetchQuotesFailed
import be.kunlabora.crafters.kunlaquota.data.QuoteRepositoryStub
import be.kunlabora.crafters.kunlaquota.service.Either.Left
import be.kunlabora.crafters.kunlaquota.service.domain.AddQuote
import be.kunlabora.crafters.kunlaquota.service.domain.Quote
import be.kunlabora.crafters.kunlaquota.service.domain.QuoteRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class QuotesTest {
    private val quoteRepositoryStub = QuoteRepositoryStub()
    private val quotes = Quotes(quoteRepository = quoteRepositoryStub)

    @Test
    fun `View all quotes`() {
        val snarfsQuote = aSingleLineQuote(name = "Snarf", text = "snarf snarf").save()
        val lionosQuote = aSingleLineQuote(name = "Lion-O", text = "stfu Snarf").save()

        val actual = quotes.findAll().valueOrThrow()

        assertThat(actual).containsExactlyInAnyOrder(
            snarfsQuote,
            lionosQuote,
        )
    }

    @Test
    fun `View all quotes but there's something wrong with the db, a failure is returned`() {
        aSingleLineQuote(name = "Snarf", text = "snarf snarf").save()

        quoteRepositoryStub.failOnNext(QuoteRepository::findAll.name, FetchQuotesFailed)

        val actual = quotes.findAll()

        assertThat(actual).isEqualTo(Left(FetchQuotesFailed))
    }

    @Test
    fun `When adding a quote fails, a failure is returned`() {
        quoteRepositoryStub.failOnNext(QuoteRepository::store.name, AddQuoteFailed("💩"))

        val actual = quotes.execute(AddQuote(lines = listOf(Quote.Line(1, name = "Snarf", text = "Snarf snarf"))))

        assertThat(actual).isEqualTo(Left(AddQuoteFailed("💩")))
    }

    @Test
    fun `When adding a quote succeeds, the quote is returned`() {
        assertThat(quoteRepositoryStub.findAll().valueOrThrow()).isEmpty()

        val actual = quotes.execute(AddQuote(lines = listOf(Quote.Line(1, name = "Snarf", text = "Snarf snarf"))))

        val expectedQuote = quoteRepositoryStub.findAll().valueOrThrow().first()

        assertThat(actual).isEqualTo(Either.Right(expectedQuote))
    }

    @Test
    fun `When adding a multiline quote succeeds, the quote is returned`() {
        assertThat(quoteRepositoryStub.findAll().valueOrThrow()).isEmpty()

        val actual = quotes.execute(AddQuote(lines = listOf(
            Quote.Line(1, name = "Snarf", text = "Snarf snarf"),
            Quote.Line(2, name = "Lion-O", text = "STFU Snarf"),
        )))

        val expectedId = quoteRepositoryStub.findAll().valueOrThrow().first().id
        val expectedQuote = aMultiLineQuote(id = expectedId) {
            "Snarf" said "Snarf snarf"
            "Lion-O" said "STFU Snarf"
        }

        assertThat(actual).isEqualTo(Either.Right(expectedQuote))
    }

    private fun Quote.save() =
        quoteRepositoryStub.store(this).valueOrThrow()
}
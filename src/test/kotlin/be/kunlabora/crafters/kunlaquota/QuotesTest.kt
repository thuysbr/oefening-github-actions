package be.kunlabora.crafters.kunlaquota

import be.kunlabora.crafters.kunlaquota.data.QuoteRepositoryStub
import be.kunlabora.crafters.kunlaquota.service.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class QuotesTest {
    private val quoteRepositoryStub = QuoteRepositoryStub()
    private val quotes = Quotes(quoteRepository = quoteRepositoryStub)

    @Test
    fun `View all quotes`() {
        val snarfsQuote = aDefaultQuote(name = "Snarf", text = "snarf snarf").save().valueOrThrow()
        val lionosQuote = aDefaultQuote(name = "Lion-O", text = "stfu Snarf").save().valueOrThrow()

        val actual = quotes.findAll()

        assertThat(actual).containsExactlyInAnyOrder(
            snarfsQuote,
            lionosQuote,
        )
    }

    @Test
    fun `When adding a quote fails, a failure is returned`() {
        quoteRepositoryStub.failOnNext(QuoteRepository::store.name, AddQuoteFailed)

        val actual = quotes.execute(AddQuote(name = "Snarf", text = "Snarf snarf"))

        assertThat(actual).isEqualTo(Either.Left(AddQuoteFailed))
    }

    @Test
    fun `When adding a quote succeeds, the quote is returned`() {
        assertThat(quoteRepositoryStub.findAll()).isEmpty()

        val actual = quotes.execute(AddQuote(name = "Snarf", text = "Snarf snarf"))

        val expectedQuote = quoteRepositoryStub.findAll().first()

        assertThat(actual).isEqualTo(Either.Right(expectedQuote))
    }

    private fun Quote.save() =
        quoteRepositoryStub.store(this)
}


fun aDefaultQuote(
    name: String = "snarf",
    text: String = "snarf snarf",
) = Quote(
    id = QuoteId.new(),
    name = name,
    text = text
)

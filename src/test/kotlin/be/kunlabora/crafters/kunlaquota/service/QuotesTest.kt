package be.kunlabora.crafters.kunlaquota.service

import be.kunlabora.crafters.kunlaquota.FetchQuotesFailed
import be.kunlabora.crafters.kunlaquota.QuoteAlreadyExists
import be.kunlabora.crafters.kunlaquota.data.QuoteRepositoryFake
import be.kunlabora.crafters.kunlaquota.service.Result.Error
import be.kunlabora.crafters.kunlaquota.service.domain.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class QuotesTest {
    private val quoteRepositoryFake = QuoteRepositoryFake()
    private val quoteShareRepositoryFake = QuoteShareRepositoryFake()
    private val quoteShareProvider = HashedQuoteShareProvider()

    private val quotes = Quotes(
        quoteRepository = quoteRepositoryFake,
        quoteShareProvider = quoteShareProvider,
        quoteShareRepository = quoteShareRepositoryFake,
    )

    @Test
    fun `View all quotes`() {
        val oldestDate: LocalDateTime = LocalDateTime.now()
        val oldestQuote: Quote = aSingleLineQuote(name = "Snarf", text = "snarf snarf", at = oldestDate).save()

        val newerDate = oldestDate.plusSeconds(1)
        val newerQuote = aSingleLineQuote(name = "Lion-O", text = "stfu Snarf", at = newerDate).save()

        val actual = quotes.findAll().valueOrThrow()

        assertThat(actual).containsExactly(
            newerQuote,
            oldestQuote,
        )
    }

    @Test
    fun `View all quotes - different style of given when then`() {
        val (oldestQuote, newestQuote) = LocalDateTime.now().let {
            Pair(
                aSingleLineQuote(name = "Snarf", text = "snarf snarf", at = it).save(),
                aSingleLineQuote(name = "Lion-O", text = "stfu Snarf", at = it.plusSeconds(1)).save()
            )
        }

        val actual = quotes.findAll().valueOrThrow()

        assertThat(actual).containsExactly(
            newestQuote,
            oldestQuote,
        )
    }

    @Test
    fun `View all quotes but there's something wrong with the db, a failure is returned`() {
        aSingleLineQuote(name = "Snarf", text = "snarf snarf").save()

        quoteRepositoryFake.failOnNext(QuoteRepository::findAll.name, FetchQuotesFailed)

        val actual = quotes.findAll()

        assertThat(actual).isEqualTo(Error(FetchQuotesFailed))
    }

    @Test
    fun `When adding a quote fails, a failure is returned`() {
        quoteRepositoryFake.failOnNext(QuoteRepository::store.name, QuoteAlreadyExists("💩"))

        val actual = quotes.execute(AddQuote(lines = listOf(Quote.Line(1, name = "Snarf", text = "Snarf snarf"))))

        assertThat(actual).isEqualTo(Error(QuoteAlreadyExists("💩")))
    }

    @Test
    fun `When adding a quote succeeds, the quote is returned`() {
        assertThat(quoteRepositoryFake.findAll().valueOrThrow()).isEmpty()

        val actual = quotes.execute(AddQuote(lines = listOf(Quote.Line(1, name = "Snarf", text = "Snarf snarf"))))

        val expectedQuote = quoteRepositoryFake.findAll().valueOrThrow().first()

        assertThat(actual).isEqualTo(Result.Ok(expectedQuote))
    }

    @Test
    fun `When adding a multiline quote succeeds, the quote is returned`() {
        assertThat(quoteRepositoryFake.findAll().valueOrThrow()).isEmpty()
        val now = LocalDateTime.of(2024, 5, 4, 1, 2, 3)

        val actual = quotes.execute(AddQuote(
            lines = listOf(
                Quote.Line(1, name = "Snarf", text = "Snarf snarf"),
                Quote.Line(2, name = "Lion-O", text = "STFU Snarf"),
            )
        ), dateProvider = { now })

        val expectedId = quoteRepositoryFake.findAll().valueOrThrow().first().id
        val expectedQuote = aMultiLineQuote(id = expectedId, at = now) {
            "Snarf" said "Snarf snarf"
            "Lion-O" said "STFU Snarf"
        }

        assertThat(actual).isEqualTo(Result.Ok(expectedQuote))
    }

    @Test
    fun `When sharing the same Quote for a second time, the same QuoteShare is returned`() {
        val quoteId = QuoteId.fromString<Quote>("749aa702-046d-44e1-b600-e7cefa1299e0")
        aSingleLineQuote(quoteId = quoteId).save()

        quotes.execute(ShareQuote(id = quoteId))
            .also { assertThat(it).isEqualTo(Result.Ok(QuoteShare("GIQYTPQ"))) }

        quotes.execute(ShareQuote(id = quoteId))
            .also { assertThat(it).isEqualTo(Result.Ok(QuoteShare("GIQYTPQ"))) }
    }

    private fun Quote.save() =
        quoteRepositoryFake.store(this).valueOrThrow()
}
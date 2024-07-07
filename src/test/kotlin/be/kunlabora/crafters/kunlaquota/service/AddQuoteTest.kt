package be.kunlabora.crafters.kunlaquota.service

import be.kunlabora.crafters.kunlaquota.QuoteIsInvalid
import be.kunlabora.crafters.kunlaquota.service.Result.Error
import be.kunlabora.crafters.kunlaquota.service.domain.Quote
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class AddQuoteTest {
    @Test
    fun `AddQuote does not validate when one of the lines doesn't contain a name`() {
        val result = AddQuote(
            lines = listOf(
                Quote.Line(1, name = "Snarf", text = "Snarf snarf"),
                Quote.Line(2, name = "", text = "Snarf snarf"),
                Quote.Line(3, name = "Snarf", text = "Snarf snarf"),
            )
        ).validate()

        assertThat(result).isEqualTo(Error(QuoteIsInvalid("A Quote Line needs a name.")))
    }

    @Test
    fun `When adding a quote fails because of one of the lines doesn't contain a text, a failure is returned`() {
        val result = AddQuote(
            lines = listOf(
                Quote.Line(1, name = "Snarf", text = "Snarf snarf"),
                Quote.Line(2, name = "Snarf", text = ""),
                Quote.Line(3, name = "Snarf", text = "Snarf snarf"),
            )
        ).validate()

        assertThat(result).isEqualTo(Error(QuoteIsInvalid("A Quote Line needs text.")))
    }

    @Test
    fun `When adding a quote fails because the lines' orders are not unique, a failure is returned`() {
        val result = AddQuote(
            lines = listOf(
                Quote.Line(1, name = "Snarf", text = "Snarf snarf"),
                Quote.Line(1, name = "Snarf", text = "Snarf snarf"),
            )
        ).validate()

        assertThat(result).isEqualTo(Error(QuoteIsInvalid("Can't have multiple lines with the same order.")))
    }
}
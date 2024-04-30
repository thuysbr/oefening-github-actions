package be.kunlabora.crafters.kunlaquota.data

import be.kunlabora.crafters.kunlaquota.AddQuoteFailed
import be.kunlabora.crafters.kunlaquota.service.Either
import be.kunlabora.crafters.kunlaquota.service.aMultiLineQuote
import be.kunlabora.crafters.kunlaquota.service.aSingleLineQuote
import be.kunlabora.crafters.kunlaquota.service.domain.Quote
import be.kunlabora.crafters.kunlaquota.service.domain.QuoteRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

abstract class QuoteRepositoryContractTest(private val quoteRepository: QuoteRepository) {

    @Test
    fun `can store quotes`() {
        val aQuote = aSingleLineQuote(name = "Jonesuuu", "When I kotlin, I kotlin. When I java, I java. But when I javascript.. I die a little on the inside.")

        val storedQuote = quoteRepository.store(aQuote).valueOrThrow()

        assertThat(storedQuote).isEqualTo(aQuote)
    }

    @Test
    fun `can store multiline quotes`() {
        val aQuote = aMultiLineQuote {
            "Joker" said "Why so serious? :)"
            "Batman" said "WHERE IS SHE?!"
        }

        val storedQuote = quoteRepository.store(aQuote).valueOrThrow()

        assertThat(storedQuote).isEqualTo(aQuote)
    }

    @Test
    fun `can fetch quotes`() {
        val quote1 = aSingleLineQuote(name = "Joker", "Why so serious? :)").save()
        val quote2 = aSingleLineQuote(name = "Uncle Ben", "With great power comes great responsibility").save()

        val quotes = quoteRepository.findAll().valueOrThrow()

        assertThat(quotes).containsExactlyInAnyOrder(quote1, quote2)
    }

    @Test
    fun `storing an already existing quote shouldn't work`() {
        val aStoredQuote = aSingleLineQuote(name = "Joker", "Why so serious? :)").save()

        val actual = quoteRepository.store(aStoredQuote)

        assertThat(actual).isEqualTo(Either.Left(AddQuoteFailed("Quote already exists!")))
    }

    private fun Quote.save() : Quote = quoteRepository.store(this).valueOrThrow()
}
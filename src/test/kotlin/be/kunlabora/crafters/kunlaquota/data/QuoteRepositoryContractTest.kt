package be.kunlabora.crafters.kunlaquota.data

import be.kunlabora.crafters.kunlaquota.AddQuoteFailed
import be.kunlabora.crafters.kunlaquota.service.Either
import be.kunlabora.crafters.kunlaquota.service.aDefaultQuote
import be.kunlabora.crafters.kunlaquota.service.domain.Quote
import be.kunlabora.crafters.kunlaquota.service.domain.QuoteRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

abstract class QuoteRepositoryContractTest(private val quoteRepository: QuoteRepository) {

    @Test
    fun `can store quotes`() {
        val aQuote = aDefaultQuote(name = "Joker", "Why so serious? :)")

        val storedQuote = quoteRepository.store(aQuote).valueOrThrow()

        assertThat(storedQuote).isEqualTo(aQuote)
    }

    @Test
    fun `can fetch quotes`() {
        val quote1 = aDefaultQuote(name = "Joker", "Why so serious? :)").save()
        val quote2 = aDefaultQuote(name = "Uncle Ben", "With great power comes great responsibility").save()

        val quotes = quoteRepository.findAll().valueOrThrow()

        assertThat(quotes).containsExactlyInAnyOrder(quote1, quote2)
    }

    @Test
    fun `storing an already existing quote shouldn't work`() {
        val aStoredQuote = aDefaultQuote(name = "Joker", "Why so serious? :)").save()

        val actual = quoteRepository.store(aStoredQuote)

        assertThat(actual).isEqualTo(Either.Left(AddQuoteFailed("Quote already exists!")))
    }

    private fun Quote.save() : Quote = quoteRepository.store(this).valueOrThrow()
}
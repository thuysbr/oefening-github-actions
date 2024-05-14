package be.kunlabora.crafters.kunlaquota.data

import be.kunlabora.crafters.kunlaquota.service.Result
import be.kunlabora.crafters.kunlaquota.service.SubtractingQuoteShareProvider
import be.kunlabora.crafters.kunlaquota.service.domain.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

abstract class QuoteShareRepositoryContractTest(
    private val quoteShareRepository: QuoteShareRepository,
    private val subtractingProvider: QuoteShareProvider = SubtractingQuoteShareProvider(expectedQuoteShare = QuoteShare("123"))
) {

    @Test
    fun `when nothing exists for the given id and we upserting it, a new QuoteShare is created and stored`() {
        val quoteId = QuoteId.new<Quote>()

        assertThat(quoteShareRepository.find(quoteId)).isEqualTo(Result.Ok(null))

        quoteShareRepository.upsert(quoteId, subtractingProvider)

        assertThat(quoteShareRepository.find(quoteId)).isEqualTo(Result.Ok(QuoteShare("123")))
    }

    @Test
    fun `when something already exists for the given id and we upserting it, the existing QuoteShare is simply returned`() {
        val quoteId = QuoteId.new<Quote>()
        quoteShareRepository.upsert(quoteId, subtractingProvider)
        assertThat(quoteShareRepository.find(quoteId)).isEqualTo(Result.Ok(QuoteShare("123")))

        quoteShareRepository.upsert(quoteId, subtractingProvider)
        assertThat(quoteShareRepository.find(quoteId)).isEqualTo(Result.Ok(QuoteShare("123")))
    }
}
package be.kunlabora.crafters.kunlaquota.service

import be.kunlabora.crafters.kunlaquota.service.domain.CanShareQuotes
import be.kunlabora.crafters.kunlaquota.service.domain.QuoteId
import be.kunlabora.crafters.kunlaquota.service.domain.QuoteRepository
import be.kunlabora.crafters.kunlaquota.service.domain.QuoteShare
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ServiceConfig {
    @Bean
    fun quotes(
        quoteRepository: QuoteRepository,
        quoteShareProvider: CanShareQuotes,
    ) = Quotes(quoteRepository, quoteShareProvider)

    @Bean
    fun quoteShareProvider(): CanShareQuotes =
        object : CanShareQuotes {
            override fun invoke(quoteId: QuoteId): QuoteShare = QuoteShare(quoteId.value)
        }
}
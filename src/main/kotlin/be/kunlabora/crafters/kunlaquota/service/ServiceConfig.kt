package be.kunlabora.crafters.kunlaquota.service

import be.kunlabora.crafters.kunlaquota.service.domain.QuoteRepository
import be.kunlabora.crafters.kunlaquota.service.domain.QuoteShare
import be.kunlabora.crafters.kunlaquota.service.domain.QuoteShareProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ServiceConfig {

    @Bean
    fun quotes(
        quoteRepository: QuoteRepository,
        quoteShareProvider: QuoteShareProvider,
    ) = Quotes(quoteRepository, quoteShareProvider)

    @Bean
    fun quoteShareProvider(): QuoteShareProvider = { quoteId -> QuoteShare(quoteId.value) }
}
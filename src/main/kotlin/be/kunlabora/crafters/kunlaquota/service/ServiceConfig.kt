package be.kunlabora.crafters.kunlaquota.service

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ServiceConfig {
    @Bean
    fun quotes(quoteRepository: QuoteRepository) =
        Quotes(quoteRepository)

    @Bean
    fun quoteRepository() =
        QuoteRepository()
}
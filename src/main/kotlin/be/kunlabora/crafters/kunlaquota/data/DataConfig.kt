package be.kunlabora.crafters.kunlaquota.data

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories

@Configuration
@EnableJdbcRepositories(basePackages = ["be.kunlabora.crafters.kunlaquota.data"])
class DataConfig {

    @Bean
    fun quoteRepository(jdbcAggregateTemplate: JdbcAggregateTemplate, quoteDAO: QuoteDAO) =
        DBQuoteRepository(jdbcAggregateTemplate, quoteDAO)

}
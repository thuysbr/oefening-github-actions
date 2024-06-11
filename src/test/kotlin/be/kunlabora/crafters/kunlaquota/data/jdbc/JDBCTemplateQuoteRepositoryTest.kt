package be.kunlabora.crafters.kunlaquota.data.jdbc

import be.kunlabora.crafters.kunlaquota.data.PostgresContainerConfig
import be.kunlabora.crafters.kunlaquota.data.QuoteRepositoryContractTest
import be.kunlabora.crafters.kunlaquota.service.domain.QuoteRepository
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.testcontainers.containers.PostgreSQLContainer
import javax.sql.DataSource

@Configuration
class JdbcTemplateConfig {

    @Bean
    fun dataSource(postgresContainer: PostgreSQLContainer<*>): DataSource {
        val jdbcUrl = postgresContainer.jdbcUrl
        return DataSourceBuilder.create()
            .url(jdbcUrl)
            .username(postgresContainer.username)
            .password(postgresContainer.password)
            .driverClassName("org.postgresql.Driver")
            .build()
    }

    @Bean
    fun jdbcTemplate(dataSource: DataSource) = NamedParameterJdbcTemplate(dataSource)

    @Bean
    fun quoteRepository(jdbcTemplate: NamedParameterJdbcTemplate) : QuoteRepository =
        JDBCTemplateQuoteRepository(jdbcTemplate)

    @Bean
    fun flyway(dataSource: DataSource): Flyway {
        return Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration")
            .load()
    }
}

@SpringBootTest(classes = [
    JdbcTemplateConfig::class,
    PostgresContainerConfig::class,
])
class JDBCTemplateQuoteRepositoryTest(
    @Autowired val quoteRepository: JDBCTemplateQuoteRepository,
    @Autowired private val flyway: Flyway,
) : QuoteRepositoryContractTest(quoteRepository) {

    @BeforeEach
    fun setUp() {
        val migrateResult = flyway.migrate()
        if (!migrateResult.success) {
            fail { migrateResult.warnings.joinToString("\n") }
        }
    }
}
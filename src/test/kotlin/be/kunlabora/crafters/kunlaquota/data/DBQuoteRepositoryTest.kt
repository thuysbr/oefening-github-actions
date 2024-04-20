package be.kunlabora.crafters.kunlaquota.data

import be.kunlabora.crafters.kunlaquota.service.aDefaultQuote
import be.kunlabora.crafters.kunlaquota.service.domain.QuoteRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.jdbc.AutoConfigureDataJdbc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName


@TestConfiguration(proxyBeanMethods = false)
class PostgresContainerConfig {
    @Bean
    @ServiceConnection
    fun postgresContainer(): PostgreSQLContainer<*> {
        return PostgreSQLContainer(DockerImageName.parse("postgres:latest"))
    }
}

@SpringBootTest(classes = [
    DataConfig::class,
    PostgresContainerConfig::class,
])
@AutoConfigureDataJdbc
class DBQuoteRepositoryTest(
    @Autowired private val quoteRepository: QuoteRepository
) {

    @Test
    fun `can save quotes`() {
        val aQuote = aDefaultQuote(name = "Joker", "Why so serious? :)")

        val storedQuote = quoteRepository.store(aQuote).valueOrThrow()

        assertThat(storedQuote).isEqualTo(aQuote)
    }
}
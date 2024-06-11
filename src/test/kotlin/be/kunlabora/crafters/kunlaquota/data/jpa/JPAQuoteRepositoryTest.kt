package be.kunlabora.crafters.kunlaquota.data.jpa

import be.kunlabora.crafters.kunlaquota.data.PostgresContainerConfig
import be.kunlabora.crafters.kunlaquota.data.QuoteRepositoryContractTest
import be.kunlabora.crafters.kunlaquota.service.domain.QuoteRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories


@Configuration
@EnableJpaRepositories(basePackages = ["be.kunlabora.crafters.kunlaquota.data.jpa"])
@EntityScan("be.kunlabora.crafters.kunlaquota.data.jpa")
class JPAConfig {

    @Bean
    fun quoteRepository(quoteDAO: QuoteDAOJPA) : QuoteRepository =
        DBJPAQuoteRepository(quoteDAO)
}

@SpringBootTest(classes = [
    JPAConfig::class,
    PostgresContainerConfig::class,
])
@AutoConfigureDataJpa
class JPAQuoteRepositoryTest(
    @Autowired quoteRepository: QuoteRepository
) : QuoteRepositoryContractTest(quoteRepository)
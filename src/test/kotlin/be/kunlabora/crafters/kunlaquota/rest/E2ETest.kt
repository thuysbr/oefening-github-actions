package be.kunlabora.crafters.kunlaquota.rest

import be.kunlabora.crafters.kunlaquota.TestKunlaquotaApplication
import be.kunlabora.crafters.kunlaquota.service.AddQuote
import be.kunlabora.crafters.kunlaquota.service.ShareQuote
import be.kunlabora.crafters.kunlaquota.service.domain.Quote
import be.kunlabora.crafters.kunlaquota.service.domain.QuoteId
import be.kunlabora.crafters.kunlaquota.service.domain.QuoteShare
import be.kunlabora.crafters.kunlaquota.service.domain.QuoteShareProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.test.jdbc.JdbcTestUtils
import java.net.URI

object DummyQuoteShareProvider : QuoteShareProvider {
    override operator fun invoke(quoteId: QuoteId)  = QuoteShare("fixed")
}

@TestConfiguration
class ShareProviderConfig {
    @Bean
    @Primary
    fun dummyQuoteShareProvider() : QuoteShareProvider = DummyQuoteShareProvider
}

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [
        ShareProviderConfig::class,
        TestKunlaquotaApplication::class,
    ]
)
class E2ETest(
    @Autowired private val restTemplate: TestRestTemplate,
    @Autowired private val jdbcOperations: JdbcOperations,
) {

    @BeforeEach
    fun setUp() {
        JdbcTestUtils.deleteFromTables(jdbcOperations, "quote")
    }

    @Test
    fun `adding and fetching quotes`() {
        val lines = listOf(
            Quote.Line(1, "Lion-o", "STFU Snarf!"),
            Quote.Line(2, "Snarf", "schnarf schnarrrff"),
        )
        val addQuote = AddQuote(lines)
        val newLocation = restTemplate.postForLocation("/api/quote", addQuote)
        assertThat(newLocation.path).isNotEmpty()

        val response = restTemplate.exchange<List<Quote>>("/api/quote", HttpMethod.GET)

        assertThat(response.body?.map { it.id.value }).containsExactly(newLocation.lastSegment())
        assertThat(response.body?.first()?.lines).containsExactlyElementsOf(lines)
    }

    @Test
    fun `fetching quotes should return them in order of newest to oldest`() {
        val oldestQuote = AddQuote(listOf(Quote.Line(1, "Snarf", "schnarf schnarrrff")))
        val oldestLocation = restTemplate.postForLocation("/api/quote", oldestQuote)
        assertThat(oldestLocation.path).isNotEmpty()

        val newestQuote = AddQuote(listOf(Quote.Line(1, "Lion-o", "STFU Snarf!")))
        val newestLocation = restTemplate.postForLocation("/api/quote", newestQuote)
        assertThat(newestLocation.path).isNotEmpty()

        val response = restTemplate.exchange<List<Quote>>("/api/quote", HttpMethod.GET)

        assertThat(response.body?.map { it.id.value })
            .containsExactly(newestLocation.lastSegment(), oldestLocation.lastSegment())
    }

    @Test
    fun `adding multiple lines with the same order returns a helpful error`() {
        val lines = listOf(
            Quote.Line(1, "Lion-o", "STFU Snarf!"),
            Quote.Line(1, "Snarf", "schnarf schnarrrff"),
        )
        val addQuote = AddQuote(lines)
        val expectedError = restTemplate.exchange<AddQuoteError>("/api/quote", HttpMethod.POST, HttpEntity(addQuote))
        assertThat(expectedError.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(expectedError.body).isEqualTo(AddQuoteError(message = "Can't have multiple lines with the same order."))

        val response = restTemplate.exchange<List<Quote>>("/api/quote", HttpMethod.GET)
        assertThat(response.body?.toList()).isEmpty()
    }

    @Test
    @Disabled("until implemented")
    fun `a quote can be shared with an easily shareable http link`() {
        val lines = listOf(
            Quote.Line(1, "Lion-o", "STFU Snarf!"),
            Quote.Line(2, "Snarf", "schnarf schnarrrff"),
        )
        val addQuote = AddQuote(lines)
        val newLocation = restTemplate.postForLocation("/api/quote", addQuote)
        assertThat(newLocation.path).isNotEmpty()

        println("Posting to $newLocation")
        val sharedLocation = restTemplate.postForLocation(newLocation, ShareQuote(QuoteId.fromString(newLocation.lastSegment())))
        assertThat(sharedLocation.lastSegment()).isEqualTo("fixed")
    }
}

fun URI.append(path: String): URI = this.resolve(this.lastSegment()+"/").resolve(path)
fun URI.lastSegment() = path.substringAfterLast('/')

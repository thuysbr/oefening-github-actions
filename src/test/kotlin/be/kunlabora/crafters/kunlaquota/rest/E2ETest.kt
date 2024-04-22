package be.kunlabora.crafters.kunlaquota.rest

import be.kunlabora.crafters.kunlaquota.TestKunlaquotaApplication
import be.kunlabora.crafters.kunlaquota.service.domain.AddQuote
import be.kunlabora.crafters.kunlaquota.service.domain.Quote
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [TestKunlaquotaApplication::class]
)
class E2ETest(
    @Autowired private val restTemplate: TestRestTemplate,
) {

    @Disabled("failing until implemented in DBRepository")
    @Test
    fun `adding and fetching quotes`() {
        val lines = listOf(
            Quote.Line(1, "Lion-o", "STFU Snarf!"),
            Quote.Line(2, "Snarf", "schnarf schnarrrff"),
        )
        val addQuote = AddQuote(lines)
        val newLocation = restTemplate.postForLocation("/api/quote", addQuote)
        assertThat(newLocation.path).isNotEmpty()

        val response: ResponseEntity<List<Quote>> = restTemplate.exchange("/api/quote", HttpMethod.GET)

        assertThat(response.body?.map { it.id.value }).containsExactly(newLocation.path.substringAfterLast('/'))
        assertThat(response.body?.first()?.lines).containsExactlyElementsOf(lines)
    }
}
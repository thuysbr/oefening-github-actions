package be.kunlabora.crafters.kunlaquota.rest

import be.kunlabora.crafters.kunlaquota.TestKunlaquotaApplication
import be.kunlabora.crafters.kunlaquota.service.domain.AddQuote
import be.kunlabora.crafters.kunlaquota.service.domain.Quote
import org.assertj.core.api.Assertions.assertThat
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

    @Test
    fun `adding and fetching quotes`() {
        val newLocation = restTemplate.postForLocation("/api/quote", AddQuote("Lion-o", "STFU Snarf!"))
        assertThat(newLocation.path).isNotEmpty()

        val response: ResponseEntity<List<Quote>> = restTemplate.exchange("/api/quote", HttpMethod.GET)

        assertThat(response.body?.map { it.id.value }).containsExactly(newLocation.path.substringAfterLast('/'))
    }
}
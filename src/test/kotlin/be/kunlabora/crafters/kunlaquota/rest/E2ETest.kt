package be.kunlabora.crafters.kunlaquota.rest

import be.kunlabora.crafters.kunlaquota.TestKunlaquotaApplication
import be.kunlabora.crafters.kunlaquota.service.Quote
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [TestKunlaquotaApplication::class]
)
class E2ETest(
    @Autowired val restTemplate: TestRestTemplate,
) {

    @Test
    fun `fetching quotes when there haven't been any added`() {
        val quotes: List<Quote> = restTemplate.getForEntity<List<Quote>>("/api/quote").body!!

        Assertions.assertThat(quotes).isEmpty()
    }
}
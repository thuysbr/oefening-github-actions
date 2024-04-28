package be.kunlabora.crafters.kunlaquota.rest

import be.kunlabora.crafters.kunlaquota.*
import be.kunlabora.crafters.kunlaquota.service.AddQuote
import be.kunlabora.crafters.kunlaquota.service.Either
import be.kunlabora.crafters.kunlaquota.service.IQuotes
import be.kunlabora.crafters.kunlaquota.service.ShareQuote
import be.kunlabora.crafters.kunlaquota.service.domain.Quote
import be.kunlabora.crafters.kunlaquota.service.domain.QuoteShare
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@TestConfiguration
class StubConfig {
    @Bean
    fun quotes(): IQuotes = FailingQuotesStub()

    class FailingQuotesStub: IQuotes {
        override fun execute(addQuote: AddQuote): Either<AddFailure, Quote> {
            return Either.Left(AddQuoteFailed("💩"))
        }

        override fun execute(shareQuote: ShareQuote): Either<ShareFailure, QuoteShare> {
            return Either.Left(ShareQuoteFailed)
        }

        override fun findAll(): Either<FetchQuotesFailed, List<Quote>> {
            return Either.Left(FetchQuotesFailed)
        }
    }
}


@WebMvcTest(WebConfig::class)
@ContextConfiguration(classes = [StubConfig::class])
@ExtendWith(OutputCaptureExtension::class)
class ApiRoutesTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper,
) {

    @Test
    fun `Failures that occur when adding a quote are logged and transformed to a 500`(capturedOutput: CapturedOutput) {
        mockMvc.post("/api/quote") {
            contentType = MediaType.APPLICATION_JSON
            content = AddQuote(listOf(Quote.Line(1, "Lion-o", "STFU Snarf!"))).toJson()
        }.andExpect {
            status { isInternalServerError() }
        }

        assertThat(capturedOutput.out).contains("💩")
    }

    @Test
    fun `Failures that occur when fetching quotes are logged and transformed to a 500`(capturedOutput: CapturedOutput) {
        mockMvc.get("/api/quote") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isInternalServerError() }
        }

        assertThat(capturedOutput.out).contains("FetchQuotesFailed")
    }

    private fun Any.toJson(): String = objectMapper.writeValueAsString(this)
}
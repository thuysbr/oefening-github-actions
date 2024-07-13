package be.kunlabora.crafters.kunlaquota.web.ui.screens

import be.kunlabora.crafters.kunlaquota.service.domain.Quote
import be.kunlabora.crafters.kunlaquota.web.ui.partial
import be.kunlabora.crafters.kunlaquota.web.ui.screens.ShowQuotesScreen.showQuotes
import org.junit.jupiter.api.Test
import verify

class ShowQuotesScreenTest {

    @Test
    fun `showQuotes when no quotes found`() {
        val quotes = emptyList<Quote>()
        partial { showQuotes(quotes) }.verify()
    }
}
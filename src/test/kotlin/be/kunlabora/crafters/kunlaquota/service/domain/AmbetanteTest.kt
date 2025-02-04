package be.kunlabora.crafters.kunlaquota.service.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.text.NumberFormat


class AmbetanteTest {

    @Test
    fun testLocalizationDifference() {
        val value = 1234.56

        val formatter: NumberFormat = NumberFormat.getInstance()
        val formatted: String = formatter.format(value)

        assertThat(formatted).isEqualTo("1.234,56")
    }
}
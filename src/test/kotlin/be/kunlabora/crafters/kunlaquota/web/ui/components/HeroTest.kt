package be.kunlabora.crafters.kunlaquota.web.ui.components

import be.kunlabora.crafters.kunlaquota.web.ui.components.Hero.hero
import be.kunlabora.crafters.kunlaquota.web.ui.partial
import org.approvaltests.Approvals
import org.junit.jupiter.api.Test

class HeroTest {
    @Test
    fun `verify hero partial`() {
        val actual = partial { hero() }
        Approvals.verify(actual)
    }
}
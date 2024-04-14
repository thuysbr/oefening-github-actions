package be.kunlabora.crafters.kunlaquota.rest

import org.springframework.web.servlet.function.RouterFunctionDsl
import org.springframework.web.servlet.function.ServerResponse

val apiRoutes: RouterFunctionDsl.() -> Unit = {
    "/quote".nest {
        GET { ServerResponse.ok().body("Hello World") }
    }
}

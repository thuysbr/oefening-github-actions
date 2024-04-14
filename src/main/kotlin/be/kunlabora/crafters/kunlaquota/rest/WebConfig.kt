package be.kunlabora.crafters.kunlaquota.rest

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.function.router

@Configuration
@EnableWebMvc
class WebConfig : WebMvcConfigurer {

    @Bean
    fun quotes(): Quotes = Quotes()

    @Bean
    fun apiRoutesBean(quotes: Quotes) = router {
        "/api".nest(apiRoutes(quotes))
    }
}
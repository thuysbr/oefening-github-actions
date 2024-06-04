package be.kunlabora.crafters.kunlaquota.rest

import be.kunlabora.crafters.kunlaquota.service.IQuotes
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.function.router


@Configuration
@EnableWebMvc
class WebConfig : WebMvcConfigurer {

    @Bean
    fun apiRoutesBean(quotes: IQuotes, objectMapper: ObjectMapper) = router {
        "/api".nest(apiRoutes(quotes, objectMapper))
    }
}
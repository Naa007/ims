package com.stepup.ims.config;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import static com.stepup.ims.constants.UIRoutingConstants.RETURN_TO_DEFAULT_ERROR;

@Configuration
public class CustomErrorPageConfiguration {

    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webServerCustomizer() {
        return factory -> {
            factory.addErrorPages(
                    new ErrorPage(HttpStatus.UNAUTHORIZED, "/error/401"), // Map 401 errors
                    new ErrorPage(HttpStatus.NOT_FOUND, RETURN_TO_DEFAULT_ERROR), // Map 404 errors
                    new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, RETURN_TO_DEFAULT_ERROR), // Map 500 errors
                    new ErrorPage(RETURN_TO_DEFAULT_ERROR) // Map all other errors
            );
        };
    }
}

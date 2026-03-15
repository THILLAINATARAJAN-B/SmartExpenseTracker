package com.smartexpense.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Smart Expense Tracker API")
                        .version("1.0.0")
                        .description("REST API for managing personal expenses with budget alerts")
                        .contact(new Contact()
                                .name("SmartExpense Dev")
                                .email("dev@smartexpense.com")));
    }
}

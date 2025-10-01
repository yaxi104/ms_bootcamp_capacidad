package co.com.reactive.api.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi capacityApi() {
        return GroupedOpenApi.builder()
                .group("capacity")
                .pathsToMatch("/api/v1/capacity")
                .build();
    }
}

package co.com.reactive.api.config;

import co.com.reactive.api.Handler;
import co.com.reactive.api.RouterRest;
import co.com.reactive.usecase.capacity.CapacityUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;

@ContextConfiguration(classes = {RouterRest.class, ConfigTest.HandlerTestConfig.class, CorsConfig.class, SecurityHeadersConfig.class})
@WebFluxTest
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void corsConfigurationShouldAllowOrigins() {
        webTestClient.post()
                .uri("/api/v1/capacity")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }

    @Configuration
    static class HandlerTestConfig {
        @Bean
        public CapacityUseCase capacityUseCase() {
            CapacityUseCase mockUseCase = Mockito.mock(CapacityUseCase.class);
            Mockito.when(mockUseCase.saveCapacity(any())).thenReturn(Mono.empty());
            return mockUseCase;
        }

        @Bean
        public Handler handler(CapacityUseCase capacityUseCase) {
            return new Handler(capacityUseCase);
        }
    }
}
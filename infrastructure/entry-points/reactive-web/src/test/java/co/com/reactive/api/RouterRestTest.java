package co.com.reactive.api;

import co.com.reactive.usecase.capacity.CapacityUseCase;
import co.com.reactive.usecase.capacitybootcamp.CapacityBootcampUseCase;
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

@ContextConfiguration(classes = {RouterRest.class, RouterRestTest.HandlerTestConfig.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testListenPOSTUseCase() {
        webTestClient.post()
                .uri("/api/v1/capacity")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody().isEmpty();
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
        public CapacityBootcampUseCase capacityBootcampUseCase() {
            CapacityBootcampUseCase mockUseCase = Mockito.mock(CapacityBootcampUseCase.class);
            Mockito.when(mockUseCase.saveCapacityBootcamp(any())).thenReturn(Mono.empty());
            return mockUseCase;
        }

        @Bean
        public Handler handler(CapacityUseCase capacityUseCase, CapacityBootcampUseCase capacityBootcampUseCase) {
            return new Handler(capacityUseCase, capacityBootcampUseCase);
        }
    }
}
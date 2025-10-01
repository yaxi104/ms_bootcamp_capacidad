package co.com.reactive.api.exception;

import co.com.reactive.usecase.capacity.exception.BadRequestException;
import co.com.reactive.usecase.capacity.exception.CapacityAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

class GlobalErrorHandlerTest {

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        RouterFunction<ServerResponse> routes = route(GET("/bad-request"), req -> Mono.error(new BadRequestException()))
                .andRoute(GET("/conflict"), req -> Mono.error(new CapacityAlreadyExistsException()))
                .andRoute(GET("/generic-error"), req -> Mono.error(new RuntimeException("Generic error")));

        WebExceptionHandler errorHandler = new GlobalErrorHandler();

        var strategies = HandlerStrategies.builder()
                .exceptionHandler(errorHandler)
                .build();

        webTestClient = WebTestClient.bindToRouterFunction(routes)
                .handlerStrategies(strategies)
                .build();
    }

    @Test
    void shouldReturnBadRequestForBadRequestException() {
        webTestClient.get().uri("/bad-request")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("The request contains invalid data. Please check the submitted fields and try again");
    }

    @Test
    void shouldReturnConflictForCapacityAlreadyExistsException() {
        webTestClient.get().uri("/conflict")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Capacity with this name already exists");
    }

    @Test
    void shouldReturnInternalServerErrorForGenericException() {
        webTestClient.get().uri("/generic-error")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Unexpected error occurred");
    }
}

package co.com.reactive.api;

import co.com.reactive.model.capacity.CapacityReq;
import co.com.reactive.model.capacity.CapacityResponse;
import co.com.reactive.model.capacity.PageResponse;
import co.com.reactive.usecase.capacity.CapacityUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HandlerTest {

    @Test
    void listenPOSTCapacityUseCaseTest() {
        CapacityUseCase useCase = mock(CapacityUseCase.class);
        when(useCase.saveCapacity(any())).thenReturn(Mono.empty());

        Handler handler = new Handler(useCase);

        CapacityReq capacityReq = new CapacityReq();
        capacityReq.setName("name");
        capacityReq.setDescription("descripcion");
        MockServerRequest request = MockServerRequest.builder()
                .method(HttpMethod.POST)
                .uri(URI.create("/api/v1/capacity"))
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(capacityReq));

        Mono<ServerResponse> responseMono = handler.listenPOSTCapacityUseCase(request);

        ServerResponse response = responseMono.block();

        assertThat(response).isNotNull();
        assertThat(response.statusCode().value()).isEqualTo(201);
    }

    @Test
    void listenGETCapacityUseCaseTest() {
        CapacityUseCase useCase = mock(CapacityUseCase.class);
        Handler handler = new Handler(useCase);

        CapacityResponse capacity1 = new CapacityResponse(1L, "Java", "Backend", List.of(), 0);
        CapacityResponse capacity2 = new CapacityResponse(2L, "Python", "Data", List.of(), 0);

        PageResponse<CapacityResponse> pageResponse = new PageResponse<>(
                List.of(capacity1, capacity2),
                0,
                5,
                2L
        );

        when(useCase.findAllCapacityPage(any(), anyString(), anyString()))
                .thenReturn(Mono.just(pageResponse));

        MockServerRequest request = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .uri(URI.create("/api/v1/capacity?sortBy=name&order=ascendente&page=0&size=5"))
                .build();

        Mono<ServerResponse> responseMono = handler.listenGETCapacityUseCase(request);

        StepVerifier.create(responseMono)
                .assertNext(response -> assertThat(response.statusCode().value()).isEqualTo(200))
                .verifyComplete();

        verify(useCase).findAllCapacityPage(any(), anyString(), anyString());
    }

}
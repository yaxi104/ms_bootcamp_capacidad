package co.com.reactive.api;

import co.com.reactive.model.capacity.CapacityReq;
import co.com.reactive.model.capacity.CapacityResponse;
import co.com.reactive.model.capacity.PageResponse;
import co.com.reactive.usecase.capacity.CapacityUseCase;
import co.com.reactive.usecase.capacitybootcamp.CapacityBootcampUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HandlerTest {

    @Mock
    private CapacityUseCase capacityUseCase;

    @Mock
    private CapacityBootcampUseCase capacityBootcampUseCase;

    private Handler handler;

    @BeforeEach
    void setUp() {
        handler = new Handler(capacityUseCase, capacityBootcampUseCase);
    }

    @Test
    void listenPOSTCapacityUseCaseTest() {
        CapacityReq capacityReq = new CapacityReq();
        capacityReq.setName("name");
        capacityReq.setDescription("descripcion");

        when(capacityUseCase.saveCapacity(any())).thenReturn(Mono.empty());

        ServerRequest request = MockServerRequest.builder()
                .method(HttpMethod.POST)
                .uri(URI.create("/api/v1/capacity"))
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(capacityReq));

        Mono<ServerResponse> responseMono = handler.listenPOSTCapacityUseCase(request);

        StepVerifier.create(responseMono)
                .assertNext(response -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED))
                .verifyComplete();

        verify(capacityUseCase).saveCapacity(any());
    }

    @Test
    void listenGETCapacityUseCaseTest() {
        CapacityResponse capacity1 = new CapacityResponse(1L, "Java", "Backend", List.of(), 0);
        CapacityResponse capacity2 = new CapacityResponse(2L, "Python", "Data", List.of(), 0);

        PageResponse<CapacityResponse> pageResponse = new PageResponse<>(
                List.of(capacity1, capacity2),
                0,
                5,
                2L
        );

        when(capacityUseCase.findAllCapacityPage(any(), anyString(), anyString()))
                .thenReturn(Mono.just(pageResponse));

        ServerRequest request = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .uri(URI.create("/api/v1/capacity?sortBy=name&order=ascendente&page=0&size=5"))
                .build();

        Mono<ServerResponse> responseMono = handler.listenGETCapacityUseCase(request);

        StepVerifier.create(responseMono)
                .assertNext(response -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK))
                .verifyComplete();

        verify(capacityUseCase).findAllCapacityPage(any(), anyString(), anyString());
    }
}
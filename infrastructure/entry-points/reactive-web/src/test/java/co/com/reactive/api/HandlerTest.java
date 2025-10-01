package co.com.reactive.api;

import co.com.reactive.model.capacity.CapacityReq;
import co.com.reactive.usecase.capacity.CapacityUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

class HandlerTest {

    @Test
    void listenPOSTCapacityUseCaseTest() {
        CapacityUseCase useCase = Mockito.mock(CapacityUseCase.class);
        Mockito.when(useCase.saveCapacity(any())).thenReturn(Mono.empty());

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
}
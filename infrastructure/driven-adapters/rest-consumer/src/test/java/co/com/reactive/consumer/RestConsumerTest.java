package co.com.reactive.consumer;

import co.com.reactive.model.capacity.CapacityTechnology;
import co.com.reactive.model.capacity.TechnologyResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RestConsumerTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WebClient mockWebClient;

    @Mock
    private RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RequestBodySpec requestBodySpec;

    @Mock
    private RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private ResponseSpec responseSpec;

    @InjectMocks
    private RestConsumer restConsumer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCapacityTechnologyRelationsSuccess() {
        CapacityTechnology relation1 = new CapacityTechnology(1L, 100L);
        CapacityTechnology relation2 = new CapacityTechnology(2L, 101L);

        when(mockWebClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/technology/list")).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(BodyInserter.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());

        Mono<Void> result = restConsumer.createCapacityTechnologyRelations(Flux.just(relation1, relation2));

        StepVerifier.create(result)
                .verifyComplete();

        verify(mockWebClient).post();
        verify(requestBodyUriSpec).uri("/technology/list");
        verify(requestBodySpec).body(any(BodyInserter.class));
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(Void.class);
    }

    @Test
    void createCapacityTechnologyRelationsError() {
        CapacityTechnology relation = new CapacityTechnology(1L, 100L);

        when(mockWebClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/technology/list")).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(BodyInserter.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.error(new RuntimeException("Failed")));

        Mono<Void> result = restConsumer.createCapacityTechnologyRelations(Flux.just(relation));

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(mockWebClient).post();
        verify(requestBodyUriSpec).uri("/technology/list");
        verify(requestBodySpec).body(any(BodyInserter.class));
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(Void.class);
    }

    @Test
    void findByCapacityIdsSuccess() {
        List<Long> capacityIds = List.of(1L, 2L);

        TechnologyCapacityResponse tech1 = new TechnologyCapacityResponse();
        tech1.setId(101L);
        tech1.setName("Java");

        TechnologyCapacityResponse tech2 = new TechnologyCapacityResponse();
        tech2.setId(102L);
        tech2.setName("Python");

        Map<Long, List<TechnologyCapacityResponse>> rawMap = Map.of(
                1L, List.of(tech1),
                2L, List.of(tech2)
        );

        WebClient.RequestBodyUriSpec uriSpec = mockWebClient.post();
        WebClient.RequestBodySpec bodySpec = uriSpec.uri("/by-capacity-ids");
        WebClient.RequestHeadersSpec<?> headersSpec = bodySpec.bodyValue(capacityIds);
        WebClient.ResponseSpec responseSpecTest = headersSpec.retrieve();

        when(responseSpecTest.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(rawMap));

        Mono<Map<Long, List<TechnologyResponse>>> result = restConsumer.findByCapacityIds(capacityIds);

        StepVerifier.create(result)
                .assertNext(map -> {
                    assertEquals(2, map.size());
                    assertEquals("Java", map.get(1L).get(0).getName());
                    assertEquals("Python", map.get(2L).get(0).getName());
                })
                .verifyComplete();
    }

}

package co.com.reactive.consumer;

import co.com.reactive.model.capacity.CapacityTechnology;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RestConsumerTest {

    @Mock
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
}

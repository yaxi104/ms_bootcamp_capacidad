package co.com.reactive.consumer;

import co.com.reactive.model.capacity.CapacityTechnology;
import co.com.reactive.model.capacity.TechnologyResponse;
import co.com.reactive.model.capacity.gateways.ICapacityTechnologyServiceClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RestConsumer implements ICapacityTechnologyServiceClient {
    private final WebClient client;

    @Override
    @CircuitBreaker(name = "createCapacityTechnology")
    public Mono<Void> createCapacityTechnologyRelations(Flux<CapacityTechnology> relations) {
        Flux<CapacityTechnologyRequest> requestFlux = relations.map(domain ->
                new CapacityTechnologyRequest(domain.getCapacityId(), domain.getTechnologyId())
        );

        return client.post()
                .uri("/technology/list")
                .body(BodyInserters.fromPublisher(requestFlux, CapacityTechnologyRequest.class))
                .retrieve()
                .bodyToMono(Void.class);
    }

    @Override
    @CircuitBreaker(name = "getCapacityTechnology")
    public Mono<Map<Long, List<TechnologyResponse>>> findByCapacityIds(List<Long> capacityIds) {
        return client.post()
                .uri("/by-capacity-ids")
                .bodyValue(capacityIds)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<Long, List<TechnologyCapacityResponse>>>() {
                })
                .map(rawMap -> rawMap.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> entry.getValue().stream()
                                        .map(this::toResponse)
                                        .toList()
                        ))
                );
    }

    private TechnologyResponse toResponse(TechnologyCapacityResponse source) {
        return new TechnologyResponse(source.getId(), source.getName());
    }
}

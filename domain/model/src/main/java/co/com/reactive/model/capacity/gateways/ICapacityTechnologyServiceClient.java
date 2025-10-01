package co.com.reactive.model.capacity.gateways;

import co.com.reactive.model.capacity.CapacityTechnology;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ICapacityTechnologyServiceClient {
    Mono<Void> createCapacityTechnologyRelations(Flux<CapacityTechnology> relations);
}

package co.com.reactive.model.capacity.gateways;

import co.com.reactive.model.capacity.CapacityTechnology;
import co.com.reactive.model.capacity.TechnologyResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface ICapacityTechnologyServiceClient {
    Mono<Void> createCapacityTechnologyRelations(Flux<CapacityTechnology> relations);

    Mono<Map<Long, List<TechnologyResponse>>> findByCapacityIds(List<Long> capacityIds);
}

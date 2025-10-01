package co.com.reactive.model.capacity.gateways;

import co.com.reactive.model.capacity.Capacity;
import reactor.core.publisher.Mono;

public interface ICapacityRepository {

    Mono<Capacity> saveCapacity(Capacity capacity);

    Mono<Boolean> existsByName(String name);

}

package co.com.reactive.model.capacity.gateways;

import co.com.reactive.model.capacity.Capacity;
import co.com.reactive.model.capacity.PageInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ICapacityRepository {

    Mono<Capacity> saveCapacity(Capacity capacity);

    Mono<Boolean> existsByName(String name);

    Mono<Long> countAll();

    Flux<Capacity> findAllOrderedByNameAsc(PageInfo pageInfo);

    Flux<Capacity> findAllOrderedByNameDesc(PageInfo pageInfo);
}

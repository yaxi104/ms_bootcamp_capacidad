package co.com.reactive.model.capacitybootcamp.gateways;

import co.com.reactive.model.capacitybootcamp.BootcampCapacity;
import co.com.reactive.model.capacitybootcamp.CapacityBootcampRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ICapacityBootcampRepository {
    Mono<Void> saveAllCapacityBootcamp(Flux<BootcampCapacity> relations);

    Mono<BootcampCapacity> findByBootcampIdAndCapacityId(BootcampCapacity bootcampCapacity);
}

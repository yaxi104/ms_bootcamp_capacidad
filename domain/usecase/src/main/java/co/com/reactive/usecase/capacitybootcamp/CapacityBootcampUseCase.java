package co.com.reactive.usecase.capacitybootcamp;

import co.com.reactive.model.capacitybootcamp.BootcampCapacity;
import co.com.reactive.model.capacitybootcamp.gateways.ICapacityBootcampRepository;
import co.com.reactive.usecase.exception.CapacityBootcampAlreadyExistsException;
import co.com.reactive.usecase.utils.ValidateRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class CapacityBootcampUseCase {

    private final ICapacityBootcampRepository capacityBootcampRepository;

    public CapacityBootcampUseCase(ICapacityBootcampRepository capacityBootcampRepository) {
        this.capacityBootcampRepository = capacityBootcampRepository;
    }

    public Mono<Void> saveCapacityBootcamp(Flux<BootcampCapacity> capacityBootcampFlux) {
        return capacityBootcampFlux
                .flatMap(capBootcamp ->
                        ValidateRequest.checkId(capBootcamp.getCapacityId())
                                .then(ValidateRequest.checkId(capBootcamp.getBootcampId()))
                                .then(
                                        capacityBootcampRepository
                                                .findByBootcampIdAndCapacityId(capBootcamp)
                                                .flatMap(existing -> Mono.error(new CapacityBootcampAlreadyExistsException()))
                                                .switchIfEmpty(Mono.just(capBootcamp))
                                )
                                .onErrorResume(Mono::error)
                )
                .cast(BootcampCapacity.class)
                .collectList()
                .filter(list -> !list.isEmpty())
                .flatMapMany(list -> capacityBootcampRepository.saveAllCapacityBootcamp(Flux.fromIterable(list)))
                .then();
    }
}
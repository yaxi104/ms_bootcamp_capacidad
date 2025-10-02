package co.com.reactive.r2dbc.capacitybootcamp;

import co.com.reactive.model.capacitybootcamp.BootcampCapacity;
import co.com.reactive.model.capacitybootcamp.CapacityBootcampRequest;
import co.com.reactive.model.capacitybootcamp.gateways.ICapacityBootcampRepository;
import co.com.reactive.r2dbc.capacitybootcamp.entities.CapacityBootcampEntity;
import co.com.reactive.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class CapacityBootcampRepositoryAdapter extends ReactiveAdapterOperations<
        BootcampCapacity,
        CapacityBootcampEntity,
        Long,
        CapacityBootcampRepository
        > implements ICapacityBootcampRepository {
    public CapacityBootcampRepositoryAdapter(CapacityBootcampRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, BootcampCapacity.class));
    }

    @Override
    public Mono<Void> saveAllCapacityBootcamp(Flux<BootcampCapacity> relations) {
        return saveAllEntities(relations).then();
    }

    @Override
    public Mono<BootcampCapacity> findByBootcampIdAndCapacityId(BootcampCapacity bootcampCapacity) {
        return repository.findByBootcampIdAndCapacityId(bootcampCapacity.getBootcampId(), bootcampCapacity.getCapacityId())
                .map(entity -> mapper.map(entity, BootcampCapacity.class));
    }
}

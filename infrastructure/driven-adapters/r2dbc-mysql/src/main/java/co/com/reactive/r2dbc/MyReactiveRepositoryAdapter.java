package co.com.reactive.r2dbc;

import co.com.reactive.model.capacity.Capacity;
import co.com.reactive.model.capacity.gateways.ICapacityRepository;
import co.com.reactive.r2dbc.entities.CapacityEntity;
import co.com.reactive.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class MyReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Capacity,
        CapacityEntity,
        Long,
        MyReactiveRepository
        > implements ICapacityRepository {
    public MyReactiveRepositoryAdapter(MyReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Capacity.class));
    }

    @Override
    public Mono<Capacity> saveCapacity(Capacity capacity) {
        return save(capacity);
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return repository.existsByName(name);
    }

    private CapacityEntity mapDomainToEntity(Capacity capacity) {
        CapacityEntity capacityEntity = new CapacityEntity();
        capacityEntity.setName(capacityEntity.getName());
        capacityEntity.setDescription(capacityEntity.getDescription());
        return capacityEntity;
    }

    private Capacity mapEntityToDomain(CapacityEntity capacityEntity) {
        Capacity capacity = new Capacity();
        capacity.setId(capacityEntity.getId());
        capacityEntity.setName(capacityEntity.getName());
        capacityEntity.setDescription(capacityEntity.getDescription());
        return capacity;
    }
}

package co.com.reactive.r2dbc.capacity;

import co.com.reactive.model.capacity.Capacity;
import co.com.reactive.model.capacity.PageInfo;
import co.com.reactive.model.capacity.gateways.ICapacityRepository;
import co.com.reactive.r2dbc.capacity.entities.CapacityEntity;
import co.com.reactive.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class CapacityRepositoryAdapter extends ReactiveAdapterOperations<
        Capacity,
        CapacityEntity,
        Long,
        CapacityRepository
        > implements ICapacityRepository {
    public CapacityRepositoryAdapter(CapacityRepository repository, ObjectMapper mapper) {
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

    @Override
    public Mono<Long> countAll() {
        return repository.countAll();
    }

    @Override
    public Flux<Capacity> findAllOrderedByNameAsc(PageInfo pageInfo) {
        int limit = pageInfo.getSize();
        int offset = pageInfo.getPage() * pageInfo.getSize();
        return repository.findAllOrderedByNameAsc(limit, offset)
                .map(entity -> mapper.map(entity, Capacity.class));
    }

    @Override
    public Flux<Capacity> findAllOrderedByNameDesc(PageInfo pageInfo) {
        int limit = pageInfo.getSize();
        int offset = pageInfo.getPage() * pageInfo.getSize();
        return repository.findAllOrderedByNameDesc(limit, offset)
                .map(entity -> mapper.map(entity, Capacity.class));
    }
}

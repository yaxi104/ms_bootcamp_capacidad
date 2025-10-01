package co.com.reactive.r2dbc;

import co.com.reactive.model.capacity.Capacity;
import co.com.reactive.model.capacity.PageInfo;
import co.com.reactive.model.capacity.gateways.ICapacityRepository;
import co.com.reactive.r2dbc.entities.CapacityEntity;
import co.com.reactive.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
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

    @Override
    public Mono<Long> countAll() {
        return repository.countAll();
    }

    @Override
    public Flux<Capacity> findAllOrderedByNameAsc(PageInfo pageInfo) {
        return repository.findAllOrderedByNameAsc(pageInfo.getSize(), pageInfo.getPage())
                .map(entity -> mapper.map(entity, Capacity.class));
    }

    @Override
    public Flux<Capacity> findAllOrderedByNameDesc(PageInfo pageInfo) {
        return repository.findAllOrderedByNameDesc(pageInfo.getSize(), pageInfo.getPage())
                .map(entity -> mapper.map(entity, Capacity.class));
    }
}

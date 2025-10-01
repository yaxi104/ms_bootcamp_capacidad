package co.com.reactive.r2dbc;

import co.com.reactive.r2dbc.entities.CapacityEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface MyReactiveRepository extends ReactiveCrudRepository<CapacityEntity, Long>, ReactiveQueryByExampleExecutor<CapacityEntity> {
    Mono<Boolean> existsByName(String name);
}

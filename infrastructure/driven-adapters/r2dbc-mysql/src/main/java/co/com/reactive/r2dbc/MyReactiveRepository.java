package co.com.reactive.r2dbc;

import co.com.reactive.r2dbc.entities.CapacityEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MyReactiveRepository extends ReactiveCrudRepository<CapacityEntity, Long>, ReactiveQueryByExampleExecutor<CapacityEntity> {
    Mono<Boolean> existsByName(String name);

    @Query("SELECT * FROM CAPACIDADES ORDER BY nombre ASC LIMIT :limit OFFSET :offset")
    Flux<CapacityEntity> findAllOrderedByNameAsc(int limit, int offset);

    @Query("SELECT * FROM CAPACIDADES ORDER BY nombre DESC LIMIT :limit OFFSET :offset")
    Flux<CapacityEntity> findAllOrderedByNameDesc(int limit, int offset);

    @Query("SELECT COUNT(*) FROM CAPACIDADES")
    Mono<Long> countAll();

}


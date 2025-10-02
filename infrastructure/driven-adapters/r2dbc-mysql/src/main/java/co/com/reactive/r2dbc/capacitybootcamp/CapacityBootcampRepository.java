package co.com.reactive.r2dbc.capacitybootcamp;

import co.com.reactive.r2dbc.capacitybootcamp.entities.CapacityBootcampEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CapacityBootcampRepository extends ReactiveCrudRepository<CapacityBootcampEntity, Long>, ReactiveQueryByExampleExecutor<CapacityBootcampEntity> {

    Mono<CapacityBootcampEntity> findByBootcampIdAndCapacityId(Long bootcampId, Long capacityId);

}


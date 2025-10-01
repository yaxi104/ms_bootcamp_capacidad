package co.com.reactive.r2dbc;

import co.com.reactive.model.capacity.Capacity;
import co.com.reactive.r2dbc.entities.CapacityEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyReactiveRepositoryAdapterTest {

    @InjectMocks
    MyReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    MyReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    void saveTest() {
        Capacity capacity = new Capacity(1L, "Arquitectura de microservicios", "Capacidad para diseñar e implementar microservicios");

        CapacityEntity capacityEntity = new CapacityEntity();
        capacityEntity.setId(1L);
        capacityEntity.setName(capacity.getName());
        capacityEntity.setDescription(capacity.getDescription());

        when(repository.save(any(CapacityEntity.class))).thenReturn(Mono.just(capacityEntity));

        Mono<Capacity> result = repositoryAdapter.saveCapacity(capacity);

        StepVerifier.create(result)
                .expectNext(capacity)
                .verifyComplete();
    }

    @Test
    void existsByNameTest() {
        when(repository.existsByName("Name")).thenReturn(Mono.just(true));

        Mono<Boolean> result = repositoryAdapter.existsByName("Name");

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }
}

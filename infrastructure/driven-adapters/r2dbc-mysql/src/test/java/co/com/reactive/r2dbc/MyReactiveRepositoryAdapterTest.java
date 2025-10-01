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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyReactiveRepositoryAdapterTest {

    @InjectMocks
    MyReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    MyReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    @Test
    void saveCapacityTest() {
        Capacity capacity = new Capacity(1L, "Arquitectura de microservicios", "Capacidad para diseñar e implementar microservicios");
        CapacityEntity capacityEntity = new CapacityEntity(1L, capacity.getName(), capacity.getDescription());

        when(mapper.map(capacity, CapacityEntity.class)).thenReturn(capacityEntity);
        when(repository.save(capacityEntity)).thenReturn(Mono.just(capacityEntity));
        when(mapper.map(capacityEntity, Capacity.class)).thenReturn(capacity);

        Mono<Capacity> result = repositoryAdapter.saveCapacity(capacity);

        StepVerifier.create(result)
                .expectNext(capacity)
                .verifyComplete();

        verify(mapper).map(capacity, CapacityEntity.class);
        verify(mapper).map(capacityEntity, Capacity.class);
        verify(repository).save(capacityEntity);
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

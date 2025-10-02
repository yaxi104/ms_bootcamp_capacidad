package co.com.reactive.r2dbc;

import co.com.reactive.model.capacity.Capacity;
import co.com.reactive.model.capacity.PageInfo;
import co.com.reactive.r2dbc.capacity.CapacityRepository;
import co.com.reactive.r2dbc.capacity.CapacityRepositoryAdapter;
import co.com.reactive.r2dbc.capacity.entities.CapacityEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyReactiveRepositoryAdapterTest {

    @InjectMocks
    CapacityRepositoryAdapter repositoryAdapter;

    @Mock
    CapacityRepository repository;

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

    @Test
    void countAllTest() {
        when(repository.countAll()).thenReturn(Mono.just(5L));

        Mono<Long> result = repositoryAdapter.countAll();

        StepVerifier.create(result)
                .expectNext(5L)
                .verifyComplete();

        verify(repository).countAll();
    }

    @Test
    void findAllOrderedByNameAscTest() {
        PageInfo pageInfo = new PageInfo(1, 2);
        CapacityEntity entity1 = new CapacityEntity(1L, "Alpha", "Desc1");
        CapacityEntity entity2 = new CapacityEntity(2L, "Beta", "Desc2");

        Capacity domain1 = new Capacity(1L, "Alpha", "Desc1");
        Capacity domain2 = new Capacity(2L, "Beta", "Desc2");

        when(repository.findAllOrderedByNameAsc(2, 2)).thenReturn(Flux.just(entity1, entity2));
        when(mapper.map(entity1, Capacity.class)).thenReturn(domain1);
        when(mapper.map(entity2, Capacity.class)).thenReturn(domain2);

        Flux<Capacity> result = repositoryAdapter.findAllOrderedByNameAsc(pageInfo);

        StepVerifier.create(result)
                .expectNext(domain1)
                .expectNext(domain2)
                .verifyComplete();

        verify(repository).findAllOrderedByNameAsc(2, 2);
        verify(mapper).map(entity1, Capacity.class);
        verify(mapper).map(entity2, Capacity.class);
    }

    @Test
    void findAllOrderedByNameDescTest() {
        PageInfo pageInfo = new PageInfo(0, 3);
        CapacityEntity entity1 = new CapacityEntity(3L, "Zeta", "Desc3");
        CapacityEntity entity2 = new CapacityEntity(2L, "Gamma", "Desc2");
        CapacityEntity entity3 = new CapacityEntity(1L, "Beta", "Desc1");

        Capacity domain1 = new Capacity(3L, "Zeta", "Desc3");
        Capacity domain2 = new Capacity(2L, "Gamma", "Desc2");
        Capacity domain3 = new Capacity(1L, "Beta", "Desc1");

        when(repository.findAllOrderedByNameDesc(3, 0)).thenReturn(Flux.just(entity1, entity2, entity3));
        when(mapper.map(entity1, Capacity.class)).thenReturn(domain1);
        when(mapper.map(entity2, Capacity.class)).thenReturn(domain2);
        when(mapper.map(entity3, Capacity.class)).thenReturn(domain3);

        Flux<Capacity> result = repositoryAdapter.findAllOrderedByNameDesc(pageInfo);

        StepVerifier.create(result)
                .expectNext(domain1)
                .expectNext(domain2)
                .expectNext(domain3)
                .verifyComplete();

        verify(repository).findAllOrderedByNameDesc(3, 0);
        verify(mapper).map(entity1, Capacity.class);
        verify(mapper).map(entity2, Capacity.class);
        verify(mapper).map(entity3, Capacity.class);
    }

}

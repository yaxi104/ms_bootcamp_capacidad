package co.com.reactive.usecase.capacity;

import co.com.reactive.model.capacity.Capacity;
import co.com.reactive.model.capacity.CapacityReq;
import co.com.reactive.model.capacity.PageInfo;
import co.com.reactive.model.capacity.TechnologyResponse;
import co.com.reactive.model.capacity.gateways.ICapacityRepository;
import co.com.reactive.model.capacity.gateways.ICapacityTechnologyServiceClient;
import co.com.reactive.usecase.exception.BadRequestException;
import co.com.reactive.usecase.exception.CapacityAlreadyExistsException;
import co.com.reactive.usecase.utils.ValidateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CapacityUseCaseTest {

    @Mock
    private ICapacityRepository capacityRepository;

    @Mock
    private ICapacityTechnologyServiceClient capacityTechnologyServiceClient;

    private CapacityUseCase capacityUseCase;

    @BeforeEach
    void setUp() {
        capacityUseCase = new CapacityUseCase(capacityRepository, capacityTechnologyServiceClient);
    }

    @Test
    void saveCapacitySuccessfully() {
        CapacityReq request = new CapacityReq("Java", "Backend", List.of(1L, 2L));
        Capacity capacity = new Capacity(1L, "Java", "Backend");

        try (MockedStatic<ValidateRequest> mockedStatic = Mockito.mockStatic(ValidateRequest.class)) {
            mockedStatic.when(() -> ValidateRequest.validateCapacityRequest(request)).thenReturn(Mono.empty());

            when(capacityRepository.existsByName("Java")).thenReturn(Mono.just(false));
            when(capacityRepository.saveCapacity(any())).thenReturn(Mono.just(capacity));
            when(capacityTechnologyServiceClient.createCapacityTechnologyRelations(any()))
                    .thenReturn(Mono.empty());

            StepVerifier.create(capacityUseCase.saveCapacity(Mono.just(request)))
                    .verifyComplete();

            verify(capacityRepository).saveCapacity(any());
            verify(capacityTechnologyServiceClient).createCapacityTechnologyRelations(any());
        }
    }

    @Test
    void saveCapacityAlreadyExistsTest() {
        CapacityReq request = new CapacityReq("Java", "Backend", List.of(1L));

        try (MockedStatic<ValidateRequest> mockedStatic = Mockito.mockStatic(ValidateRequest.class)) {
            mockedStatic.when(() -> ValidateRequest.validateCapacityRequest(request)).thenReturn(Mono.empty());

            when(capacityRepository.existsByName("Java")).thenReturn(Mono.just(true));

            StepVerifier.create(capacityUseCase.saveCapacity(Mono.just(request)))
                    .expectError(CapacityAlreadyExistsException.class)
                    .verify();

            verify(capacityRepository, never()).saveCapacity(any());
        }
    }

    @Test
    void saveErrorIfValidationFails() {
        CapacityReq request = new CapacityReq(null, null, null);

        try (MockedStatic<ValidateRequest> mockedStatic = Mockito.mockStatic(ValidateRequest.class)) {
            mockedStatic.when(() -> ValidateRequest.validateCapacityRequest(request))
                    .thenReturn(Mono.error(new BadRequestException()));

            when(capacityRepository.existsByName(any())).thenReturn(Mono.just(false));

            StepVerifier.create(capacityUseCase.saveCapacity(Mono.just(request)))
                    .expectError(BadRequestException.class)
                    .verify();
        }
    }

    @Test
    void saveCapacityFails() {
        CapacityReq request = new CapacityReq("Java", "Backend", List.of(1L));
        try (MockedStatic<ValidateRequest> mockedStatic = Mockito.mockStatic(ValidateRequest.class)) {
            mockedStatic.when(() -> ValidateRequest.validateCapacityRequest(request)).thenReturn(Mono.empty());

            when(capacityRepository.existsByName("Java")).thenReturn(Mono.just(false));
            when(capacityRepository.saveCapacity(any()))
                    .thenReturn(Mono.error(new RuntimeException("DB error")));

            StepVerifier.create(capacityUseCase.saveCapacity(Mono.just(request)))
                    .expectError(RuntimeException.class)
                    .verify();
        }
    }

    @Test
    void createRelationsFails() {
        CapacityReq request = new CapacityReq("Java", "Backend", List.of(1L, 2L));
        Capacity capacity = new Capacity(1L, "Java", "Backend");

        try (MockedStatic<ValidateRequest> mockedStatic = Mockito.mockStatic(ValidateRequest.class)) {
            mockedStatic.when(() -> ValidateRequest.validateCapacityRequest(request)).thenReturn(Mono.empty());

            when(capacityRepository.existsByName("Java")).thenReturn(Mono.just(false));
            when(capacityRepository.saveCapacity(any())).thenReturn(Mono.just(capacity));
            when(capacityTechnologyServiceClient.createCapacityTechnologyRelations(any()))
                    .thenReturn(Mono.error(new RuntimeException("Relations failed")));

            StepVerifier.create(capacityUseCase.saveCapacity(Mono.just(request)))
                    .expectError(RuntimeException.class)
                    .verify();
        }
    }

    @Test
    void findAllCapacityPageShouldReturnSortedByNameAsc() {
        PageInfo pageInfo = new PageInfo(0, 2);
        String sortBy = "name";
        String order = "ascendente";

        Capacity cap1 = new Capacity(1L, "Alpha", "Desc1");
        Capacity cap2 = new Capacity(2L, "Beta", "Desc2");

        TechnologyResponse tech1 = new TechnologyResponse(101L, "Java");
        TechnologyResponse tech2 = new TechnologyResponse(102L, "Python");

        when(capacityRepository.countAll()).thenReturn(Mono.just(2L));
        when(capacityRepository.findAllOrderedByNameAsc(pageInfo)).thenReturn(Flux.just(cap1, cap2));

        Map<Long, List<TechnologyResponse>> techMap = Map.of(
                1L, List.of(tech1),
                2L, List.of(tech2)
        );

        when(capacityTechnologyServiceClient.findByCapacityIds(List.of(1L, 2L)))
                .thenReturn(Mono.just(techMap));

        StepVerifier.create(capacityUseCase.findAllCapacityPage(pageInfo, sortBy, order))
                .assertNext(response -> {
                    assertEquals(2, response.getContent().size());
                    assertEquals("Alpha", response.getContent().get(0).getName());
                    assertEquals("Beta", response.getContent().get(1).getName());
                    assertEquals(1, response.getContent().get(0).getTechnologyCount());
                    assertEquals(1, response.getContent().get(1).getTechnologyCount());
                    assertEquals(2L, response.getTotalElements());
                })
                .verifyComplete();

        verify(capacityRepository).countAll();
        verify(capacityRepository).findAllOrderedByNameAsc(pageInfo);
        verify(capacityTechnologyServiceClient).findByCapacityIds(List.of(1L, 2L));
    }

    @Test
    void findAllCapacityPageShouldSortByTechnologyCountDesc() {
        PageInfo pageInfo = new PageInfo(0, 2);
        String sortBy = "technologyCount";
        String order = "descendente";

        Capacity cap1 = new Capacity(1L, "Alpha", "Desc1");
        Capacity cap2 = new Capacity(2L, "Beta", "Desc2");

        TechnologyResponse tech1 = new TechnologyResponse(101L, "Java");
        TechnologyResponse tech2 = new TechnologyResponse(102L, "Python");
        TechnologyResponse tech3 = new TechnologyResponse(103L, "Go");

        when(capacityRepository.countAll()).thenReturn(Mono.just(2L));
        when(capacityRepository.findAllOrderedByNameDesc(pageInfo)).thenReturn(Flux.just(cap1, cap2));

        Map<Long, List<TechnologyResponse>> techMap = Map.of(
                1L, List.of(tech1),
                2L, List.of(tech2, tech3)
        );

        when(capacityTechnologyServiceClient.findByCapacityIds(List.of(1L, 2L)))
                .thenReturn(Mono.just(techMap));

        StepVerifier.create(capacityUseCase.findAllCapacityPage(pageInfo, sortBy, order))
                .assertNext(response -> {
                    assertEquals(2, response.getContent().size());
                    assertEquals(2, response.getContent().get(0).getTechnologyCount());
                    assertEquals(1, response.getContent().get(1).getTechnologyCount());
                    assertEquals(2L, response.getTotalElements());
                })
                .verifyComplete();
    }

    @Test
    void findAllCapacityPageShouldFailWithInvalidParams() {
        PageInfo pageInfo = new PageInfo(0, 2);
        String sortBy = "invalidSort";
        String order = "invalidOrder";

        StepVerifier.create(capacityUseCase.findAllCapacityPage(pageInfo, sortBy, order))
                .expectError(BadRequestException.class)
                .verify();

        verify(capacityRepository, never()).countAll();
        verify(capacityTechnologyServiceClient, never()).findByCapacityIds(any());
    }

    @Test
    void findAllCapacityPageShouldReturnEmptyContent() {
        PageInfo pageInfo = new PageInfo(0, 2);
        String sortBy = "name";
        String order = "ascendente";

        when(capacityRepository.countAll()).thenReturn(Mono.just(0L));
        when(capacityRepository.findAllOrderedByNameAsc(pageInfo)).thenReturn(Flux.empty());
        when(capacityTechnologyServiceClient.findByCapacityIds(List.of())).thenReturn(Mono.just(Map.of()));

        StepVerifier.create(capacityUseCase.findAllCapacityPage(pageInfo, sortBy, order))
                .assertNext(response -> {
                    assertEquals(0, response.getContent().size());
                    assertEquals(0L, response.getTotalElements());
                })
                .verifyComplete();
    }

}
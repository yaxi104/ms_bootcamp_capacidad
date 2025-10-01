package co.com.reactive.usecase.capacity;

import co.com.reactive.model.capacity.Capacity;
import co.com.reactive.model.capacity.CapacityReq;
import co.com.reactive.model.capacity.gateways.ICapacityRepository;
import co.com.reactive.model.capacity.gateways.ICapacityTechnologyServiceClient;
import co.com.reactive.usecase.capacity.exception.BadRequestException;
import co.com.reactive.usecase.capacity.exception.CapacityAlreadyExistsException;
import co.com.reactive.usecase.capacity.utils.ValidateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

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
}
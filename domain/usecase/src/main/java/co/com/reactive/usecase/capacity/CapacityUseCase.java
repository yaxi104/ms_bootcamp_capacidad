package co.com.reactive.usecase.capacity;

import co.com.reactive.model.capacity.Capacity;
import co.com.reactive.model.capacity.CapacityReq;
import co.com.reactive.model.capacity.CapacityTechnology;
import co.com.reactive.model.capacity.gateways.ICapacityRepository;
import co.com.reactive.model.capacity.gateways.ICapacityTechnologyServiceClient;
import co.com.reactive.usecase.capacity.exception.CapacityAlreadyExistsException;
import co.com.reactive.usecase.capacity.utils.CapacityFactory;
import co.com.reactive.usecase.capacity.utils.ValidateRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class CapacityUseCase {

    private final ICapacityRepository capacityRepository;
    private final ICapacityTechnologyServiceClient capacityTechnologyServiceClient;

    public CapacityUseCase(ICapacityRepository capacityRepository,
                           ICapacityTechnologyServiceClient capacityTechnologyServiceClient) {
        this.capacityRepository = capacityRepository;
        this.capacityTechnologyServiceClient = capacityTechnologyServiceClient;
    }

    public Mono<Void> saveCapacity(Mono<CapacityReq> capacityReqMono) {
        return capacityReqMono
                .flatMap(request ->
                        ValidateRequest.validateCapacityRequest(request)
                                .then(capacityRepository.existsByName(request.getName())
                                        .flatMap(exists -> {
                                            if (exists) {
                                                return Mono.error(new CapacityAlreadyExistsException());
                                            }
                                            return Mono.just(request);
                                        })
                                )
                )
                .flatMap(request -> {
                    Capacity capacity = CapacityFactory.toCapacityDomain(request);
                    List<Long> techIds = request.getTechnologiesIds();

                    return capacityRepository.saveCapacity(capacity)
                            .flatMap(savedCapacity -> {
                                List<CapacityTechnology> relations = techIds.stream()
                                        .map(techId -> new CapacityTechnology(savedCapacity.getId(), techId))
                                        .toList();

                                return capacityTechnologyServiceClient
                                        .createCapacityTechnologyRelations(Flux.fromIterable(relations));
                            });
                });
    }
}

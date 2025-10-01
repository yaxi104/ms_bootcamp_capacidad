package co.com.reactive.usecase.capacity;

import co.com.reactive.model.capacity.Capacity;
import co.com.reactive.model.capacity.CapacityReq;
import co.com.reactive.model.capacity.CapacityResponse;
import co.com.reactive.model.capacity.CapacityTechnology;
import co.com.reactive.model.capacity.PageInfo;
import co.com.reactive.model.capacity.PageResponse;
import co.com.reactive.model.capacity.TechnologyResponse;
import co.com.reactive.model.capacity.gateways.ICapacityRepository;
import co.com.reactive.model.capacity.gateways.ICapacityTechnologyServiceClient;
import co.com.reactive.usecase.capacity.exception.BadRequestException;
import co.com.reactive.usecase.capacity.exception.CapacityAlreadyExistsException;
import co.com.reactive.usecase.capacity.utils.CapacityFactory;
import co.com.reactive.usecase.capacity.utils.ValidateRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static co.com.reactive.usecase.capacity.utils.Constants.ORDEN_ASC;
import static co.com.reactive.usecase.capacity.utils.Constants.ORDEN_DESC;
import static co.com.reactive.usecase.capacity.utils.Constants.ORDEN_NAME;
import static co.com.reactive.usecase.capacity.utils.Constants.ORDEN_TECHN_COUNT;

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
                                            if (Boolean.TRUE.equals(exists)) {
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

    public Mono<PageResponse<CapacityResponse>> findAllCapacityPage(PageInfo pageInfo, String sortBy, String order) {
        Set<String> validOrders = Set.of(ORDEN_ASC, ORDEN_DESC);
        Set<String> validSorts = Set.of(ORDEN_NAME, ORDEN_TECHN_COUNT);

        if (!validOrders.stream().map(String::toLowerCase).collect(Collectors.toSet()).contains(order.toLowerCase()) ||
                !validSorts.stream().map(String::toLowerCase).collect(Collectors.toSet()).contains(sortBy.toLowerCase())) {
            return Mono.error(new BadRequestException());
        }

        boolean ascending = order.equalsIgnoreCase(ORDEN_ASC);
        boolean sortByTechCount = sortBy.equalsIgnoreCase(ORDEN_TECHN_COUNT);

        Mono<Long> totalMono = capacityRepository.countAll();

        Flux<Capacity> pagedFlux = ascending
                ? capacityRepository.findAllOrderedByNameAsc(pageInfo)
                : capacityRepository.findAllOrderedByNameDesc(pageInfo);

        return Mono.zip(totalMono, pagedFlux.collectList())
                .flatMap(tuple -> {
                    Long totalElements = tuple.getT1();
                    List<Capacity> capacities = tuple.getT2();
                    List<Long> capacityIds = capacities.stream().map(Capacity::getId).toList();

                    return capacityTechnologyServiceClient.findByCapacityIds(capacityIds)
                            .map(techMap -> {
                                List<CapacityResponse> content = capacities.stream()
                                        .map(capacity -> {
                                            List<TechnologyResponse> technologies = techMap.getOrDefault(capacity.getId(), List.of());
                                            return new CapacityResponse(
                                                    capacity.getId(),
                                                    capacity.getName(),
                                                    capacity.getDescription(),
                                                    technologies,
                                                    technologies.size()
                                            );
                                        })
                                        .toList();

                                if (sortByTechCount) {
                                    content = sortByTechnologyCount(content, ascending);
                                }
                                return new PageResponse<>(
                                        content,
                                        pageInfo.getPage(),
                                        pageInfo.getSize(),
                                        totalElements
                                );
                            });
                });
    }

    private List<CapacityResponse> sortByTechnologyCount(List<CapacityResponse> list, boolean ascending) {
        return list.stream()
                .sorted((c1, c2) -> ascending
                        ? Integer.compare(c1.getTechnologyCount(), c2.getTechnologyCount())
                        : Integer.compare(c2.getTechnologyCount(), c1.getTechnologyCount()))
                .toList();
    }
}
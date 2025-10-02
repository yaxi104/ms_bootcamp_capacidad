package co.com.reactive.api;

import co.com.reactive.model.capacity.CapacityReq;
import co.com.reactive.model.capacity.PageInfo;
import co.com.reactive.model.capacitybootcamp.BootcampCapacity;
import co.com.reactive.model.capacitybootcamp.CapacityBootcampRequest;
import co.com.reactive.usecase.capacity.CapacityUseCase;
import co.com.reactive.usecase.capacitybootcamp.CapacityBootcampUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static co.com.reactive.api.utils.Constants.DEFAULT_ORDER;
import static co.com.reactive.api.utils.Constants.DEFAULT_PAGE;
import static co.com.reactive.api.utils.Constants.DEFAULT_SIZE;
import static co.com.reactive.api.utils.Constants.DEFAULT_SORTBY;
import static co.com.reactive.api.utils.Constants.QUERY_ORDER;
import static co.com.reactive.api.utils.Constants.QUERY_PAGE;
import static co.com.reactive.api.utils.Constants.QUERY_SIZE;
import static co.com.reactive.api.utils.Constants.QUERY_SORTBY;

@Component
@RequiredArgsConstructor
public class Handler {
    private final CapacityUseCase capacityUseCase;
    private final CapacityBootcampUseCase capacityBootcampUseCase;

    public Mono<ServerResponse> listenPOSTCapacityUseCase(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CapacityReq.class)
                .flatMap(capacityReq ->
                        capacityUseCase.saveCapacity(Mono.just(capacityReq))
                                .then(ServerResponse.status(201).build())
                );
    }

    public Mono<ServerResponse> listenGETCapacityUseCase(ServerRequest serverRequest) {
        String sortBy = serverRequest.queryParam(QUERY_SORTBY).orElse(DEFAULT_SORTBY);
        String order = serverRequest.queryParam(QUERY_ORDER).orElse(DEFAULT_ORDER);
        Integer page = Integer.parseInt(serverRequest.queryParam(QUERY_PAGE).orElse(DEFAULT_PAGE));
        Integer size = Integer.parseInt(serverRequest.queryParam(QUERY_SIZE).orElse(DEFAULT_SIZE));
        PageInfo pageInfo = new PageInfo(page, size);

        return capacityUseCase.findAllCapacityPage(pageInfo, sortBy, order)
                .flatMap(pageResponse -> ServerResponse.ok().bodyValue(pageResponse));
    }

    public Mono<ServerResponse> listenPOSTCapacityBootcampUseCase(ServerRequest serverRequest) {
        return serverRequest.bodyToFlux(CapacityBootcampRequest.class)
                .collectList()
                .flatMap(list -> {
                    Flux<BootcampCapacity> domainFlux = Flux.fromIterable(
                            list.stream()
                                    .map(req -> new BootcampCapacity(req.getCapacityId(), req.getBootcampId()))
                                    .toList()
                    );
                    return capacityBootcampUseCase.saveCapacityBootcamp(domainFlux)
                            .then(ServerResponse.status(HttpStatus.CREATED).build());
                });
    }

}
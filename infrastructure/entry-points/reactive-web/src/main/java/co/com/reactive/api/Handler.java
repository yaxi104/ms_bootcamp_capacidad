package co.com.reactive.api;

import co.com.reactive.model.capacity.CapacityReq;
import co.com.reactive.usecase.capacity.CapacityUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {
    private final CapacityUseCase capacityUseCase;

    public Mono<ServerResponse> listenPOSTCapacityUseCase(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CapacityReq.class)
                .flatMap(capacityReq ->
                        capacityUseCase.saveCapacity(Mono.just(capacityReq))
                                .then(ServerResponse.status(201).build())
                );
    }
}
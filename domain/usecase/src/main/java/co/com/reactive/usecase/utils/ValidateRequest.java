package co.com.reactive.usecase.utils;

import co.com.reactive.model.capacity.CapacityReq;
import co.com.reactive.usecase.exception.BadRequestException;
import reactor.core.publisher.Mono;

import java.util.List;

import static co.com.reactive.usecase.utils.Constants.MAX_SIZE_TECHN;
import static co.com.reactive.usecase.utils.Constants.MIN_SIZE_TECHN;

public class ValidateRequest {

    private ValidateRequest() {
    }

    public static Mono<Void> checkNotBlank(String value) {
        if (value == null || value.trim().isEmpty()) {
            return Mono.error(new BadRequestException());
        }
        return Mono.empty();
    }

    public static Mono<Void> checkId(Long value) {
        if (value == null || value <= 0) {
            return Mono.error(new BadRequestException());
        }
        return Mono.empty();
    }

    public static Mono<Void> checkTechnologies(List<Long> technologies) {
        if (technologies == null || technologies.isEmpty() || technologies.size() < MIN_SIZE_TECHN || technologies.size() > MAX_SIZE_TECHN) {
            return Mono.error(new BadRequestException());
        }

        long uniqueCount = technologies.stream().distinct().count();
        if (uniqueCount != technologies.size()) {
            return Mono.error(new BadRequestException());
        }

        return Mono.empty();
    }

    public static Mono<Void> validateCapacityRequest(CapacityReq request) {
        return checkNotBlank(request.getName())
                .then(checkNotBlank(request.getDescription()))
                .then(checkTechnologies(request.getTechnologiesIds()));
    }
}
package co.com.reactive.api.exception;

import co.com.reactive.usecase.capacity.exception.BadRequestException;
import co.com.reactive.usecase.capacity.exception.CapacityAlreadyExistsException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import static co.com.reactive.api.exception.ExceptionResponse.BAD_REQUEST_MESSAGE;
import static co.com.reactive.api.exception.ExceptionResponse.CAPACITY_ALREADY_EXISTS;


@Component
@Order(-2)
public class GlobalErrorHandler implements WebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "Unexpected error occurred";

        if (ex instanceof BadRequestException) {
            status = HttpStatus.BAD_REQUEST;
            message = BAD_REQUEST_MESSAGE.getMessage();
        } else if (ex instanceof CapacityAlreadyExistsException) {
            status = HttpStatus.CONFLICT;
            message = CAPACITY_ALREADY_EXISTS.getMessage();
        }

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = String.format("{\"message\": \"%s\"}", message);
        byte[] bytes = body.getBytes();

        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory().wrap(bytes)));
    }
}
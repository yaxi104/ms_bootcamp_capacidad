package co.com.reactive.api;

import co.com.reactive.model.capacity.CapacityReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @RouterOperation(
            path = "/api/v1/capacity",
            method = RequestMethod.POST,
            beanClass = Handler.class,
            beanMethod = "listenPOSTCapacityUseCase",
            operation = @Operation(
                    operationId = "saveCapacity",
                    summary = "Save capacity",
                    requestBody = @RequestBody(
                            required = true,
                            content = @Content(schema = @Schema(implementation = CapacityReq.class))
                    ),
                    responses = {
                            @ApiResponse(responseCode = "201", description = "Created"),
                            @ApiResponse(responseCode = "400", description = "Invalid request",
                                    content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                            name = "Bad Request Example",
                                            value = "{\"message\": \"The request contains invalid data. Please check the submitted fields and try again.\"}"
                                    ))
                            ),
                            @ApiResponse(responseCode = "409", description = "Conflict - Relationship already exists",
                                    content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                            name = "Conflict Example",
                                            value = "{\"message\": \"A relationship between this technology and capacity already exists.\"}"
                                    ))
                            )}
            )
    )
    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/capacity"), handler::listenPOSTCapacityUseCase);
    }
}

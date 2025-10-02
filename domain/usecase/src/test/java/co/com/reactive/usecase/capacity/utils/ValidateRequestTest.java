package co.com.reactive.usecase.capacity.utils;

import co.com.reactive.model.capacity.CapacityReq;
import co.com.reactive.usecase.exception.BadRequestException;
import co.com.reactive.usecase.utils.Constants;
import co.com.reactive.usecase.utils.ValidateRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ValidateRequestTest {

    @Test
    void constructorIsPrivate() throws Exception {
        Constructor<ValidateRequest> constructor = ValidateRequest.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        ValidateRequest instance = constructor.newInstance();
        assertNotNull(instance);
    }

    @Test
    void checkNotBlankSuccessTest() {
        StepVerifier.create(ValidateRequest.checkNotBlank("Java"))
                .verifyComplete();
    }

    @ParameterizedTest
    @NullAndEmptySource
    void checkNotBlankFailTest(String arg) {
        StepVerifier.create(ValidateRequest.checkNotBlank(arg))
                .expectError(BadRequestException.class)
                .verify();
    }


    @Test
    void checkIdSucessTest() {
        StepVerifier.create(ValidateRequest.checkId(1L))
                .verifyComplete();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1})
    void checkIdFailTest(Long arg) {
        StepVerifier.create(ValidateRequest.checkId(arg))
                .expectError(BadRequestException.class)
                .verify();
    }

    @Test
    void checkTechnologiesSuccessTest() {
        StepVerifier.create(ValidateRequest.checkTechnologies(List.of(1L, 2L, 3L)))
                .verifyComplete();
    }

    @Test
    void checkTechnologiesShouldFailWhenNull() {
        StepVerifier.create(ValidateRequest.checkTechnologies(null))
                .expectError(BadRequestException.class)
                .verify();
    }

    @Test
    void checkTechnologiesShouldFailWhenEmpty() {
        StepVerifier.create(ValidateRequest.checkTechnologies(List.of()))
                .expectError(BadRequestException.class)
                .verify();
    }

    @Test
    void checkTechnologiesShouldFailWhenBelowMinSize() {
        StepVerifier.create(ValidateRequest.checkTechnologies(List.of()))
                .expectError(BadRequestException.class)
                .verify();
    }

    @Test
    void checkTechnologiesShouldFailWhenAboveMaxSize() {
        int tooManyElements = Constants.MAX_SIZE_TECHN + 1;
        List<Long> tooMany = IntStream.rangeClosed(1, tooManyElements).mapToObj(Long::valueOf).toList();

        StepVerifier.create(ValidateRequest.checkTechnologies(tooMany))
                .expectError(BadRequestException.class)
                .verify();
    }

    @Test
    void checkTechnologiesShouldFailWhenContainsDuplicates() {
        StepVerifier.create(ValidateRequest.checkTechnologies(List.of(1L, 2L, 2L)))
                .expectError(BadRequestException.class)
                .verify();
    }

    @Test
    void validateCapacityRequestShouldSucceed() {
        CapacityReq req = new CapacityReq("Java", "Backend", List.of(1L, 2L, 3L));
        StepVerifier.create(ValidateRequest.validateCapacityRequest(req))
                .verifyComplete();
    }

    @Test
    void validateCapacityRequestShouldFailWhenNameIsBlank() {
        CapacityReq req = new CapacityReq("   ", "Backend", List.of(1L));
        StepVerifier.create(ValidateRequest.validateCapacityRequest(req))
                .expectError(BadRequestException.class)
                .verify();
    }

    @Test
    void validateCapacityRequestShouldFailWhenDescriptionIsNull() {
        CapacityReq req = new CapacityReq("Java", null, List.of(1L));
        StepVerifier.create(ValidateRequest.validateCapacityRequest(req))
                .expectError(BadRequestException.class)
                .verify();
    }

    @Test
    void validateCapacityRequestShouldFailWhenTechListInvalid() {
        CapacityReq req = new CapacityReq("Java", "Backend", List.of());
        StepVerifier.create(ValidateRequest.validateCapacityRequest(req))
                .expectError(BadRequestException.class)
                .verify();
    }
}
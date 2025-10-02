package co.com.reactive.usecase.capacity.utils;

import co.com.reactive.usecase.utils.CapacityFactory;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CapacityFactoryTest {
    @Test
    void constructorIsPrivate() throws Exception {
        Constructor<CapacityFactory> constructor = CapacityFactory.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        CapacityFactory instance = constructor.newInstance();
        assertNotNull(instance);
    }
}
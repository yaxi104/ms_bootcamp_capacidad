package co.com.reactive.usecase.capacity.utils;

import co.com.reactive.model.capacity.Capacity;
import co.com.reactive.model.capacity.CapacityReq;

public class CapacityFactory {

    private CapacityFactory() {
    }

    public static Capacity toCapacityDomain(CapacityReq request) {
        Capacity capacity = new Capacity();
        capacity.setName(request.getName());
        capacity.setDescription(request.getDescription());
        return capacity;
    }
}

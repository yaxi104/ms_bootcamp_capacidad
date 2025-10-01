package co.com.reactive.model.capacity;

public class CapacityTechnology {

    private Long technologyId;
    private Long capacityId;

    public CapacityTechnology(Long technologyId, Long capacityId) {
        this.technologyId = technologyId;
        this.capacityId = capacityId;
    }

    public CapacityTechnology() {
    }

    public Long getTechnologyId() {
        return technologyId;
    }

    public void setTechnologyId(Long technologyId) {
        this.technologyId = technologyId;
    }

    public Long getCapacityId() {
        return capacityId;
    }

    public void setCapacityId(Long capacityId) {
        this.capacityId = capacityId;
    }
}

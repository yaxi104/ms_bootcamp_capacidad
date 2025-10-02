package co.com.reactive.model.capacitybootcamp;

public class BootcampCapacity {

    private Long capacityId;
    private Long bootcampId;

    public BootcampCapacity(Long capacityId, Long bootcampId) {
        this.capacityId = capacityId;
        this.bootcampId = bootcampId;
    }

    public BootcampCapacity() {
    }

    public Long getCapacityId() {
        return capacityId;
    }

    public void setCapacityId(Long capacityId) {
        this.capacityId = capacityId;
    }

    public Long getBootcampId() {
        return bootcampId;
    }

    public void setBootcampId(Long bootcampId) {
        this.bootcampId = bootcampId;
    }

}

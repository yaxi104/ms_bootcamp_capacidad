package co.com.reactive.model.capacity;

import java.util.List;

public class CapacityReq {
    private String name;
    private String description;
    private List<Long> technologiesIds;

    public CapacityReq(String name, String description, List<Long> technologiesIds) {
        this.name = name;
        this.description = description;
        this.technologiesIds = technologiesIds;
    }

    public CapacityReq() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getTechnologiesIds() {
        return technologiesIds;
    }

    public void setTechnologiesIds(List<Long> technologiesIds) {
        this.technologiesIds = technologiesIds;
    }
}

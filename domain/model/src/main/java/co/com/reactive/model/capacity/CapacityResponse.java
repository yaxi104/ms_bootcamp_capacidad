package co.com.reactive.model.capacity;

import java.util.List;

public class CapacityResponse {

    private Long id;

    private String name;

    private String description;

    private List<TechnologyResponse> technologies;

    private Integer technologyCount;

    public CapacityResponse(Long id, String name, String description, List<TechnologyResponse> technologies, Integer technologyCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.technologies = technologies;
        this.technologyCount = technologyCount;
    }

    public CapacityResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TechnologyResponse> getTechnologies() {
        return technologies;
    }

    public void setTechnologies(List<TechnologyResponse> technologies) {
        this.technologies = technologies;
    }

    public Integer getTechnologyCount() {
        return technologyCount;
    }

    public void setTechnologyCount(Integer technologyCount) {
        this.technologyCount = technologyCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
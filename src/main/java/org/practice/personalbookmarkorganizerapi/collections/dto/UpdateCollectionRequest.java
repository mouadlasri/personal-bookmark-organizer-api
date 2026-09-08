package org.practice.personalbookmarkorganizerapi.collections.dto;

public class UpdateCollectionRequest {
    private String name;
    private String description;

    public UpdateCollectionRequest() {}

    public UpdateCollectionRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

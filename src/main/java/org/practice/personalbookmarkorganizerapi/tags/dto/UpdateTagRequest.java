package org.practice.personalbookmarkorganizerapi.tags.dto;

public class UpdateTagRequest {
    private String name;

    public UpdateTagRequest() {}

    public UpdateTagRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

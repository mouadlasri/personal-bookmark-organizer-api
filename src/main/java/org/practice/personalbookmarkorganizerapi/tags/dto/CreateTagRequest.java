package org.practice.personalbookmarkorganizerapi.tags.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateTagRequest {
    @NotBlank
    private String name;

    public CreateTagRequest() {}

    public CreateTagRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

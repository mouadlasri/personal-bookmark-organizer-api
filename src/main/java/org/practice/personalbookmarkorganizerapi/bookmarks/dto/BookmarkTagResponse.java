package org.practice.personalbookmarkorganizerapi.bookmarks.dto;

import java.util.UUID;

public class BookmarkTagResponse {
    private UUID id;
    private String name;

    public BookmarkTagResponse(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

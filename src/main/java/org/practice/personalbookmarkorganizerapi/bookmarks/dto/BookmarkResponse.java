package org.practice.personalbookmarkorganizerapi.bookmarks.dto;

import org.practice.personalbookmarkorganizerapi.bookmarks.BookmarkStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public class BookmarkResponse {
    private UUID id;
    private String url;
    private String title;
    private String notes;
    private BookmarkStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public BookmarkResponse(UUID id, String url, String title, String notes, BookmarkStatus status, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.notes = notes;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getNotes() {
        return notes;
    }

    public BookmarkStatus getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}

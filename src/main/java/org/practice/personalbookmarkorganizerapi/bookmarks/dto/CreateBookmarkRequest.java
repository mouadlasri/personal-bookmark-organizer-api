package org.practice.personalbookmarkorganizerapi.bookmarks.dto;

import jakarta.validation.constraints.NotBlank;
import org.practice.personalbookmarkorganizerapi.bookmarks.BookmarkStatus;

public class CreateBookmarkRequest {
    @NotBlank
    private String url;
    @NotBlank
    private String title;

    private String notes;

    public CreateBookmarkRequest() {}

    public CreateBookmarkRequest(String url, String title, String notes) {
        this.url = url;
        this.title = title;
        this.notes = notes;
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

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

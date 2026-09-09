package org.practice.personalbookmarkorganizerapi.bookmarks.dto;

import jakarta.validation.constraints.NotBlank;
import org.practice.personalbookmarkorganizerapi.bookmarks.BookmarkStatus;

public class UpdateBookmarkRequest {
    private String url;
    private String title;
    private String notes;
    private BookmarkStatus status;

    public UpdateBookmarkRequest() {}

    public UpdateBookmarkRequest(String url, String title, String notes, BookmarkStatus status) {
        this.url = url;
        this.title = title;
        this.notes = notes;
        this.status = status;
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

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setStatus(BookmarkStatus status) {
        this.status = status;
    }
}

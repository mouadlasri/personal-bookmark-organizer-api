package org.practice.personalbookmarkorganizerapi.bookmarks.exception;

import org.practice.personalbookmarkorganizerapi.exception.ResourceNotFoundException;

public class BookmarkNotFoundException extends ResourceNotFoundException {
    public BookmarkNotFoundException() {
        super("Bookmark not found.");
    }
}

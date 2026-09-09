package org.practice.personalbookmarkorganizerapi.bookmarks.exception;

import org.practice.personalbookmarkorganizerapi.exception.InvalidRequestException;

public class InvalidBookmarkUrlException extends InvalidRequestException {
    public InvalidBookmarkUrlException() {
        super("Bookmark url must not be blank.");
    }
}

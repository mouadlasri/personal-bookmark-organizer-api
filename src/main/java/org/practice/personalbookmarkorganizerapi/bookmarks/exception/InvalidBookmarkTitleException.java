package org.practice.personalbookmarkorganizerapi.bookmarks.exception;

import org.practice.personalbookmarkorganizerapi.exception.InvalidRequestException;

public class InvalidBookmarkTitleException extends InvalidRequestException {
    public InvalidBookmarkTitleException() {
        super("Bookmark title must not be blank.");
    }
}

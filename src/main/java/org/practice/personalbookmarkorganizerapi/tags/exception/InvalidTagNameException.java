package org.practice.personalbookmarkorganizerapi.tags.exception;

import org.practice.personalbookmarkorganizerapi.exception.InvalidRequestException;

public class InvalidTagNameException extends InvalidRequestException {
    public InvalidTagNameException() {
        super("Tag name must not be blank.");
    }
}

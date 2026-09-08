package org.practice.personalbookmarkorganizerapi.collections.exception;

import org.practice.personalbookmarkorganizerapi.exception.InvalidRequestException;

public class InvalidCollectionNameException extends InvalidRequestException {
    public InvalidCollectionNameException() {
        super("Collection name must not be blank.");
    }
}

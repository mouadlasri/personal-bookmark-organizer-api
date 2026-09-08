package org.practice.personalbookmarkorganizerapi.collections.exception;

import org.practice.personalbookmarkorganizerapi.exception.ResourceNotFoundException;

public class CollectionNotFoundException extends ResourceNotFoundException {
    public CollectionNotFoundException() {
        super("Collection not found.");
    }
}

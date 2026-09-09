package org.practice.personalbookmarkorganizerapi.tags.exception;

import org.practice.personalbookmarkorganizerapi.exception.ResourceAlreadyExistsException;

public class TagNameAlreadyExistsException extends ResourceAlreadyExistsException {
    public TagNameAlreadyExistsException() {
        super("Tag name already exists.");
    }
}

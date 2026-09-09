package org.practice.personalbookmarkorganizerapi.tags.exception;

import org.practice.personalbookmarkorganizerapi.exception.ResourceNotFoundException;

public class TagNotFoundException extends ResourceNotFoundException {
    public TagNotFoundException() {
        super("Tag not found.");
    }
}

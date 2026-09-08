package org.practice.personalbookmarkorganizerapi.users.exception;

import org.practice.personalbookmarkorganizerapi.exception.ResourceAlreadyExistsException;

public class ProfileAlreadyExistsException extends ResourceAlreadyExistsException {
    public ProfileAlreadyExistsException() {
        super("User already exists.");
    }
}

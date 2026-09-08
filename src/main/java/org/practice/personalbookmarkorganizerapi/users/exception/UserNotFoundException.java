package org.practice.personalbookmarkorganizerapi.users.exception;

import org.practice.personalbookmarkorganizerapi.exception.ResourceNotFoundException;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException() {
        super("User not found.");
    }
}

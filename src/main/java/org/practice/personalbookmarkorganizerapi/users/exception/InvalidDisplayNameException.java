package org.practice.personalbookmarkorganizerapi.users.exception;

import org.practice.personalbookmarkorganizerapi.exception.InvalidRequestException;

public class InvalidDisplayNameException extends InvalidRequestException {
    public InvalidDisplayNameException() {
        super("Display name can't be blank.");
    }
}

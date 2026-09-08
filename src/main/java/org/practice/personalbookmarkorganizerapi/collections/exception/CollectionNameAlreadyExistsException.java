package org.practice.personalbookmarkorganizerapi.collections.exception;

import org.practice.personalbookmarkorganizerapi.exception.ResourceAlreadyExistsException;

public class CollectionNameAlreadyExistsException extends ResourceAlreadyExistsException {
   public CollectionNameAlreadyExistsException(String name) {
       super("Collection already exists with this name: " + name + ".");
   }
}

package com.project2.exception;

/**
 * Thrown when a student tries to register for an event they are already registered for.
 */
public class DuplicateRegistrationException extends RuntimeException {
    public DuplicateRegistrationException(String message) {
        super(message);
    }
}

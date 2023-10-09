package org.example.models.exceptions;

public class DuplicateRodneCisloException extends Exception{
        public DuplicateRodneCisloException() {
            super("Pojištěnec s tímto rodným číslem je již v databázi");
        }

        public DuplicateRodneCisloException(String message) {
            super(message);
        }

        public DuplicateRodneCisloException(String message, Throwable cause) {
            super(message, cause);
        }
}

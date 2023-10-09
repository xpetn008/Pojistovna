package org.example.models.exceptions;

public class DuplicateEmailException extends Exception{
    public DuplicateEmailException(String message){
        super(message);
    }
}

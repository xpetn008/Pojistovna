package org.example.models.exceptions;

public class SpatnyUdajException extends Exception{
    private String column;
    public SpatnyUdajException(String column, String message){
        super(message);
        this.column = column;
    }
    public String getColumn(){
        return column;
    }
}

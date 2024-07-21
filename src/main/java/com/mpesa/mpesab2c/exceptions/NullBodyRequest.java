package com.mpesa.mpesab2c.exceptions;

public class NullBodyRequest extends RuntimeException {
    public NullBodyRequest(String message){
        super("Null Body Request");
    }

}

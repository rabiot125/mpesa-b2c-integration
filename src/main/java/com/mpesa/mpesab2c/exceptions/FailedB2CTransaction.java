package com.mpesa.mpesab2c.exceptions;

public class FailedB2CTransaction extends RuntimeException{
    public FailedB2CTransaction(String message){
        super("Failed B2C Transaction");
    }
}


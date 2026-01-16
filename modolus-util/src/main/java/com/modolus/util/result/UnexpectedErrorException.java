package com.modolus.util.result;

public class UnexpectedErrorException extends RuntimeException {
    public UnexpectedErrorException(String message) {
        super(message);
    }

    public UnexpectedErrorException(Throwable cause) {
        super(cause);
    }

}

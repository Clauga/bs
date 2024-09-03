package com.bsn.booksphere.exception;

public class OperationNotPermittedException extends RuntimeException {
//when we make an exception we must always make a handler, since if we do not make a handler the application falls and the error cannot be seen in the console
    public OperationNotPermittedException() {
    }

    public OperationNotPermittedException(String message) {
        super(message);
    }
}

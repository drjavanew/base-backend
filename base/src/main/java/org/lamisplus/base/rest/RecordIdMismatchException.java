package org.lamisplus.base.rest;

public class RecordIdMismatchException extends RuntimeException {

    /*
        public RecordIdMismatchException() {
        }
    */
    public RecordIdMismatchException(Long id) {
        super(String.valueOf(id));
    }
/*
    public RecordIdMismatchException(String message) {
        super(message);
    }
    public RecordIdMismatchException(String message, Throwable cause) {
        super(message, cause);
    }
*/
}

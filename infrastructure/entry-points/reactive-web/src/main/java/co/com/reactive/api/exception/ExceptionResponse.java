package co.com.reactive.api.exception;

public enum ExceptionResponse {
    BAD_REQUEST_MESSAGE("The request contains invalid data. Please check the submitted fields and try again"),
    CAPACITY_ALREADY_EXISTS("Capacity with this name already exists");

    private String message;

    ExceptionResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
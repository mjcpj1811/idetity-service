package com.example.identity_service.exception;

public enum ErrorCode {
    INVALID_KEY(1001, "Invalid key"),
    USER_ALREADY_EXISTS(1002, "User already exists"),
    USERNAME_INVALID(1003, "Username must be at least 3 characters long"),
    PASSWORD_INVALID(1004, "Password must be at least 8 characters long"),
    USER_NOT_FOUND(1005, "User not found"),
    UNCATEGORIZED_EXCEPTION(9999, "An uncategorized exception occurred");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

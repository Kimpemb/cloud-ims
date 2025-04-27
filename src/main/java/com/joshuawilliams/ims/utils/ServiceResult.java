package com.joshuawilliams.ims.utils;

public class ServiceResult<T> {
    private final boolean success;
    private final String message;
    private final T data;

    // Private constructor to ensure immutability
    public ServiceResult(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    // Static helper methods for convenience

    // Success result without data
    public static <T> ServiceResult<T> success(String message) {
        return new ServiceResult<>(true, message, null);
    }

    // Success result with data
    public static <T> ServiceResult<T> success(String message, T data) {
        return new ServiceResult<>(true, message, data);
    }

    // Failure result
    public static <T> ServiceResult<T> failure(String message) {
        return new ServiceResult<>(false, message, null);
    }

    // Failure result with data (optional)
    public static <T> ServiceResult<T> failure(String message, T data) {
        return new ServiceResult<>(false, message, data);
    }
}

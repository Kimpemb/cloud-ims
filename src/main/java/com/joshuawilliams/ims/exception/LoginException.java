package com.joshuawilliams.ims.exception;

public class LoginException extends Exception {

    // Constructor that takes a message
    public LoginException(String message) {
        super(message);
    }

    // Constructor that takes a message and a cause (Throwable)
    public LoginException(String message, Throwable cause) {
        super(message, cause); // Pass the message and cause to the parent Exception class
    }
}

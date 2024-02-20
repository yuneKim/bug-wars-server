package net.crusadergames.bugwars.exception;

public class RefreshTokenException extends Exception {

    public RefreshTokenException(String token, String message) {
        super(String.format("Failed for [%s]: %s", token, message));
    }
}
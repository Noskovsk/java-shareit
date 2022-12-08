package ru.practicum.shareit.exception;

public class IncorrectStatusException extends RuntimeException {
    public IncorrectStatusException(String message) {
        super(message);
    }
}

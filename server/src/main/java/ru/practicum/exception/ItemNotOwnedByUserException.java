package ru.practicum.exception;

public class ItemNotOwnedByUserException extends RuntimeException {
    public ItemNotOwnedByUserException(String message) {
        super(message);
    }
}

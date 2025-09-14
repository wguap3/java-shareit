package ru.practicum.shareit.exception;

public class ItemNotOwnedByUserException extends RuntimeException {
    public ItemNotOwnedByUserException(String message) {
        super(message);
    }
}

package ru.practicum.exception;

public class BookingOwnItemException extends RuntimeException {
    public BookingOwnItemException(String message) {
        super(message);
    }
}

package ru.practicum.shareit.exception;

public class BookingAlreadyProcessedException extends RuntimeException {
    public BookingAlreadyProcessedException(String message) {
        super(message);
    }
}

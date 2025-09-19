package ru.practicum.exception;

public class BookingAlreadyProcessedException extends RuntimeException {
    public BookingAlreadyProcessedException(String message) {
        super(message);
    }
}

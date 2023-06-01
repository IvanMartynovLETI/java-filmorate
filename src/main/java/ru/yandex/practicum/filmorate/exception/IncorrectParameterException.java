package ru.yandex.practicum.filmorate.exception;

public class IncorrectParameterException extends RuntimeException {
    public IncorrectParameterException(String s) {
        super(s);
    }
}
package ru.yandex.practicum;

public class DictionaryIsEmptyException extends RuntimeException {
    public DictionaryIsEmptyException(String message) {
        super(message);
    }
}

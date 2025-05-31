package ru.yandex.practicum.filmorate.model.enums;

import ru.yandex.practicum.filmorate.exception.ValidationException;

public enum FilmSortBy {
    YEAR("year"),
    LIKES("likes");

    private final String value;

    FilmSortBy(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static FilmSortBy fromString(String value) {
        for (FilmSortBy sortBy : FilmSortBy.values()) {
            if (sortBy.value.equalsIgnoreCase(value)) {
                return sortBy;
            }
        }
        throw new ValidationException("Неизвестный параметр. Используйте значения 'year' или 'likes' для определения сортировки");
    }

}

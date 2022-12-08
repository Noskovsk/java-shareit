package ru.practicum.shareit;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PaginationValidator {
    public static Integer validateFrom(Integer from) {
        if (from != null && from < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неверные параметры пагинации.");
        }
        if (from == null) {
            from = 0;
        }
        return from;
    }

    public static Integer validateSize(Integer size) {
        if (size != null && size <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неверные параметры пагинации.");
        }
        if (size == null) {
            size = Integer.MAX_VALUE;
        }
        return size;
    }
}

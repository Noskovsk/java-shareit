package ru.practicum.shareit.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PaginationParams {
    public static PageRequest createPageRequest(Integer from, Integer size, String orderField) {
        System.out.println("fr" + from + ", sz" + size);
        return PageRequest.of(from / size, size, Sort.by(orderField).descending());
    }

    public static PageRequest createPageRequest(Integer from, Integer size) {
        System.out.println("fr" + from + ", sz" + size);
        return PageRequest.of(from / size, size);
    }
}

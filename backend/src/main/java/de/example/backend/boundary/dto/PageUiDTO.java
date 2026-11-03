package de.example.backend.boundary.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * One page of a result list.
 * Decouples the API from the JSON structure of Spring Data's {@link Page}.
 *
 * @param <T> Type of the elements
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PageUiDTO<T> {

    private final List<T> content;

    private final int page;

    private final int size;

    private final long totalElements;

    private final int totalPages;

    public static <T> PageUiDTO<T> from(Page<T> page) {
        return new PageUiDTO<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}

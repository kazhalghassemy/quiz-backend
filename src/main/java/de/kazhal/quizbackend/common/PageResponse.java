package de.kazhal.quizbackend.common;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponse<T>(List<T> content, // die Einträge dieser Seite
                              int page, // welche Seite (beginnt bei 0!)
                              int size, // wie viele pro Seite
                              long totalElements,// wie viele Einträge es insgesamt gibt
                              int totalPages // wie viele Seiten es insgesamt gibt
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(page.getContent(),
                page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }
}

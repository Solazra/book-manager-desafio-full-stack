package com.bookmanager.domain.book;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

public final class BookSort {

    private static final Set<String> ALLOWED = Set.of("title", "author", "year", "createdAt");
    private static final Sort DEFAULT = Sort.by(Sort.Direction.DESC, "createdAt");

    private BookSort() {
    }

    public static Pageable toPageable(int page, int size, String sort) {
        int safeSize = Math.min(Math.max(size, 1), 100);
        if (sort == null || sort.isBlank()) {
            return PageRequest.of(page, safeSize, DEFAULT);
        }
        String[] parts = sort.split(",");
        String field = parts[0].trim();
        if (!ALLOWED.contains(field)) {
            return PageRequest.of(page, safeSize, DEFAULT);
        }
        Sort.Direction dir = parts.length > 1 && parts[1].trim().equalsIgnoreCase("asc")
            ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(page, safeSize, Sort.by(dir, field));
    }
}

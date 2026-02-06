package ru.itq.reestr.dto;

import java.time.OffsetDateTime;

public record DocDto(
        Long id,
        String innerId,
        String title,
        String status,
        String createdBy,
        OffsetDateTime createdAt,
        String updatedBy,
        OffsetDateTime updatedAt) {
}

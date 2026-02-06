package ru.itq.reestr.dto;

import ru.itq.reestr.model.Action;

import java.time.OffsetDateTime;

public record HistoryDto(
        Long id,
        Long docId,
        Action action,
        String actionBy,
        OffsetDateTime actionAt,
        String description) {
}

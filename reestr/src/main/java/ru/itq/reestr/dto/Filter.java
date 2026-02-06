package ru.itq.reestr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.itq.reestr.model.Status;

import java.time.OffsetDateTime;

public record Filter(
        @Schema(description = "Статус документа", example = "DRAFT")
        Status status,
        @Schema(description = "Автор документа", example = "auto-generate")
        String createdBy,
        @Schema(description = "Дата создания документа, начало периода. Может быть не указано", example = "2026-02-02 12:03:15.210789 +00:00")
        OffsetDateTime createdAtFrom,
        @Schema(description = "Дата создания документа, окончание периода. Может быть не указано", example = "2026-02-02 12:03:15.210789 +00:00")
        OffsetDateTime createdAtTo,
        @Schema(description = "Дата изменения документа, начало периода. Может быть не указано", example = "2026-02-02 12:03:15.210789 +00:00")
        OffsetDateTime updatedAtFrom,
        @Schema(description = "Дата изменения документа, окончание периода периода. Может быть не указано", example = "2026-02-02 12:03:15.210789 +00:00")
        OffsetDateTime updatedAtTo) {
}

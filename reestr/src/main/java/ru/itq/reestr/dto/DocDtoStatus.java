package ru.itq.reestr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.itq.reestr.model.Status;

import java.time.OffsetDateTime;

public record DocDtoStatus(
        @Schema(description = "Идентификационный номер", example = "1")
        Long id,
        @Schema(description = "Внутренний номер документа", example = "FZMY2KTGPw")
        String innerId,
        @Schema(description = "Наименование документа", example = "Акт приема-передачи")
        String title,
        @Schema(description = "Статус документа", example = "DRAFT")
        Status status,
        @Schema(description = "Автор документа", example = "auto-generate")
        String createdBy,
        @Schema(description = "Время создания документа", example = "2026-02-02 12:03:15.210789 +00:00")
        OffsetDateTime createdAt,
        @Schema(description = "Автор изменений документа", example = "auto-update")
        String updatedBy,
        @Schema(description = "Время изменения документа", example = "2026-02-02 12:05:15.210789 +00:00")
        OffsetDateTime updatedAt) {
}

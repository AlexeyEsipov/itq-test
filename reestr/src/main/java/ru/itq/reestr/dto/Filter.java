package ru.itq.reestr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.PastOrPresent;
import ru.itq.reestr.model.Status;

import java.time.OffsetDateTime;

public record Filter(

        @Nullable
        @Schema(description = "Статус документа", example = "DRAFT")
        Status status,

        @Nullable
        @Schema(description = "Автор документа", example = "auto-generate")
        String createdBy,

        @PastOrPresent
        @Schema(description = "Дата создания документа, начало периода. Может быть не указано", example = "2026-02-02T12:03:15.210789Z")
        OffsetDateTime createdAtFrom,

        @PastOrPresent
        @Schema(description = "Дата создания документа, окончание периода. Может быть не указано", example = "2026-02-02T12:03:15.210789Z")
        OffsetDateTime createdAtTo,

        @PastOrPresent
        @Schema(description = "Дата изменения документа, начало периода. Может быть не указано", example = "2026-02-02T12:03:15.210789Z")
        OffsetDateTime updatedAtFrom,

        @PastOrPresent
        @Schema(description = "Дата изменения документа, окончание периода периода. Может быть не указано", example = "2026-02-02T12:03:15.210789Z")
        OffsetDateTime updatedAtTo) {
}

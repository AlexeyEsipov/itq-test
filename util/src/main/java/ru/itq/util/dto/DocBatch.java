package ru.itq.util.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record DocBatch(
        @Schema(description = "Список внутренних номеров", example = "FZMY2KTGPw, CZMH2KTGpA")
        List<String> docInnerIds,

        @Schema(description = "Кто вносит изменения", example = "auto-commit")
        String actionBy,

        @Schema(description = "Комментарий")
        String comment) {
}


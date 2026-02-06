package ru.itq.reestr.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record DocCreateDto(

        @Schema(description = "Внутренний номер документа", example = "FZMY2KTGPw")
        String innerId,

        @Schema(description = "Наименование документа", example = "Акт приема-передачи")
        String title,

        @Schema(description = "Автор документа", example = "auto-generate")
        String createdBy) {
}

package ru.itq.reestr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record DocCreateDto(

        @NotNull
        @Schema(description = "Внутренний номер документа", example = "FZMY2KTGPw")
        String innerId,

        @NotNull
        @Schema(description = "Наименование документа", example = "Акт приема-передачи")
        String title,

        @NotNull
        @Schema(description = "Автор документа", example = "auto-generate")
        String createdBy){
}

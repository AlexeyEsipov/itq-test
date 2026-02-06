package ru.itq.reestr.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ConcurrentTestRequest(

        @Schema(description = "Внутренний номер документа", example = "FZMY2KTGPw")
        String innerId,

        @Schema(description = "Количество потоков", example = "5")
        Integer threads,

        @Schema(description = "Количество попыток для каждого потока", example = "3")
        Integer attempts) {
}

package ru.itq.util.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.itq.util.model.StatusAttempt;

public record ResultAttempt(
        @Schema(description = "Внутренний номер документа", example = "FZMY2KTGPw")
        String innerId,

        @Schema(description = "Статус попытки", example = "SUCCESS")
        StatusAttempt status,

        @Schema(description = "Имя потока", example = "thread-name-pool1")
        String threadName) {
}

package ru.itq.reestr.exception;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorResponse(
        @Schema(description = "Описание ошибки", example = "объект не существует")
        String message
) {
}

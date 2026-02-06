package ru.itq.util.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record TaskStatus(

        @Schema(description = "Наименование операции (docSubmit или docApprove)", example = "docSubmit")
        String taskId,

        @Schema(description = "Запущена ли операция. true - запущена, false - не запущена")
        Boolean running) {
}

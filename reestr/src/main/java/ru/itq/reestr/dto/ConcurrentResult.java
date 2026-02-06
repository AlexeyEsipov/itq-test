package ru.itq.reestr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.itq.reestr.model.Status;

public record ConcurrentResult(

        @Schema(description = "Внутренний номер документа", example = "FZMY2KTGPw")
        String innerId,

        @Schema(description = "Имя потока", example = "thread-name-pool1")
        String winnerName,

        @Schema(description = "Общее количество попыток")
        Integer totalAttempt,

        @Schema(description = "Количество попыток, завершившихся статусом ERROR")
        Integer totalError,

        @Schema(description = "Количество попыток, завершившихся статусом NOT_FOUND")
        Integer totalNotFoundStep,

        @Schema(description = "Количество попыток, завершившихся статусом CONFLICT")
        Integer totalConflict,

        @Schema(description = "Количество попыток, завершившихся статусом SUCCESS")
        Integer totalSuccess,

        @Schema(description = "Всего шагов")
        Integer totalStep,

        @Schema(description = "Итоговый статус документа", example = "APPROVED")
        Status status) {
}

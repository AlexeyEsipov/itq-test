package ru.itq.reestr.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record BathRequest(
        @Schema(description = "Список внутренних номеров", example = "FZMY2KTGPw, CZMH2KTGpA")
        List<String> innerIds,
        @Schema(description = "Номер страницы к выдаче, начиная с 0", example = "0")
        Integer page,
        @Schema(description = "Количество документов на странице", example = "10")
        Integer size,
        @Schema(description = "Поле сортировки", example = "innerId")
        String sortField,
        @Schema(description = "Направление сортировки : asc или desc", example = "desc")
        String sortDirection) {
}

package ru.itq.reestr.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PageParam(
        @Schema(description = "Номер страницы к выдаче, начиная с 0", example = "0")
        Integer pageNumber,

        @Schema(description = "Количество документов на странице", example = "10")
        Integer pageSize,

        @Schema(description = "Поле сортировки", example = "innerId")
        String sortField,

        @Schema(description = "Направление сортировки : asc или desc", example = "desc")
        String sortDirection) {
}

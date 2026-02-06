package ru.itq.reestr.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record SearchValue(
        @Schema(description = "Набор параметров фильтрации")
        Filter filter,

        @Schema(description = "Параметры страницы")
        PageParam pageParam) {
}

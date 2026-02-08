package ru.itq.reestr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;

public record SearchValue(
        @Nullable
        @Valid
        @Schema(description = "Набор параметров фильтрации")
        Filter filter,

        @Nullable
        @Valid
        @Schema(description = "Параметры страницы")
        PageParam pageParam) {
}

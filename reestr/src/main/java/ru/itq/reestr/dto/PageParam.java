package ru.itq.reestr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.boot.context.properties.bind.DefaultValue;

public record PageParam(
        @Schema(description = "Номер страницы к выдаче, начиная с 0", example = "0")
        @PositiveOrZero
        @Nullable
        Integer pageNumber,

        @Schema(description = "Количество документов на странице", example = "10")
        @PositiveOrZero
        @Nullable
        Integer pageSize,

        @Schema(description = "Поле сортировки", example = "innerId")
        String sortField,

        @Schema(description = "Направление сортировки : asc или desc", example = "desc")
        String sortDirection) {
        public PageParam {
                if (sortField == null) {
                        sortField = "id";
                }
                if (sortDirection == null) {
                        sortDirection = "desc";
                }
        }
}

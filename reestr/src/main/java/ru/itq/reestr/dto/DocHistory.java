package ru.itq.reestr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record DocHistory(

        @JsonProperty(value = "document")
        @Schema(description = "Данные документа")
        DocDtoStatus docDto,

        @Schema(description = "Список истории действий над документом")
        List<HistoryDto> histories) {
}

package ru.itq.reestr.dto;

import ru.itq.reestr.model.Status;

public record DocIdStatus(
        Long id,
        String innerId,
        Status status) {
}

package ru.itq.reestr.dto;

public record ResultAttemptConcurrent(
        ResultAttempt attempt,
        Integer successStep,
        Integer conflictStep,
        Integer errorStep,
        Integer notFoundStep,
        Integer step) {
}

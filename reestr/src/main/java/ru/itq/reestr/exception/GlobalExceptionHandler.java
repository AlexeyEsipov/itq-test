package ru.itq.reestr.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DocumentNotFoundException.class)
    public ResponseEntity<ErrorResponce> handleResourceNotFoundException(DocumentNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponce(ex.getMessage()));
    }
}

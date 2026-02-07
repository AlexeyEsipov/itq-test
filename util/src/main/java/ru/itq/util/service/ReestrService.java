package ru.itq.util.service;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.itq.util.dto.DocBatch;
import ru.itq.util.dto.DocCreateDto;
import ru.itq.util.dto.ResultAttempt;
import ru.itq.util.exception.ClientErrorException;
import ru.itq.util.exception.ServerErrorException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReestrService {
    private final FeignService feignService;


    @Retry(name = "reestrService")
    @Bulkhead(name = "userServiceBulkhead", fallbackMethod = "bulkheadFallback")
    @RateLimiter(name = "userServiceRateLimiter", fallbackMethod = "rateLimiterFallback")
    @CircuitBreaker(name = "userServiceCircuitBreaker", fallbackMethod = "circuitBreakerFallbackCreateDocs")
    public ResponseEntity<Long> createDocument(DocCreateDto createDto) {
        return feignService.createDocument(createDto);
    }

    public ResponseEntity<List<ResultAttempt>> batchSubmit(DocBatch batch) {
        return feignService.batchSubmit(batch);
    }

    public ResponseEntity<List<ResultAttempt>> batchApprove(DocBatch batch) {
        return feignService.batchApprove(batch);
    }

    public ResponseEntity<Long> circuitBreakerFallbackCreateDocs(DocCreateDto createDto, Throwable ex) {
        if (ex instanceof ServerErrorException) {
            log.info("ReestrService is currently unavailable. - circuitBreaker is open");
            return ResponseEntity.ok(-1L);
        }
        if (ex instanceof ClientErrorException) {
            log.info("ReestrService detected error request. - circuitBreaker is open");
            return ResponseEntity.ok(-5L);
        }
        log.info("ReestrService is currently unavailable. - circuitBreaker is open");
        return ResponseEntity.ok(-1L);
    }

    private ResponseEntity<Long> rateLimiterFallback(DocCreateDto createDto, Exception e) {
        log.info("Rate Limiter: Слишком много запросов. Пожалуйста, подождите.");
        return ResponseEntity.ok(-2L);
    }

    private ResponseEntity<Long> bulkheadFallback(DocCreateDto createDto, Exception e) {
        log.info("Bulkhead: Система перегружена, нет свободных слотов для обработки.");
        return ResponseEntity.ok(-3L);
    }
}

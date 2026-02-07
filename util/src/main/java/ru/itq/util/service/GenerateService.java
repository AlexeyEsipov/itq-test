package ru.itq.util.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.itq.util.dto.DocCreateDto;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenerateService {

    @Value("${count.generated.docs:10}")
    private int totalDocGenerate;
    private static final String ACTION_BY = "generator";
    private final ReestrService reestrService;


    @Async
    public void createDoc() {
        log.info("Start generate {} docs", totalDocGenerate);
        int i = 0;
        int firstThird = totalDocGenerate/3;
        int secondThird = totalDocGenerate*2/3;
        do {
            try {
                ResponseEntity<Long> response = reestrService.createDocument(
                        new DocCreateDto(RandomWordGenerator.generateRandomWord(), "title-generate", ACTION_BY)
                );
                if (response == null || !response.hasBody()) {
                    continue;
                }
                if (response.getBody() == -1L) {
                    log.info("ReestrService is currently unavailable. - circuitBreaker is open");
                    break;
                }
                if (response.getBody() == -2L) {
                    log.info("Rate Limiter: Слишком много запросов. Пожалуйста, подождите.");
                    TimeUnit.MILLISECONDS.sleep(100L);
                    continue;
                }
                if (response.getBody() == -3L) {
                    log.info("Bulkhead: Система перегружена, нет свободных слотов для обработки.");
                    TimeUnit.MILLISECONDS.sleep(1000L);
                    continue;
                }

                if (i == firstThird) {
                    log.info("1/3 generate : {} records", i + 1);
                } else if (i == secondThird) {
                    log.info("2/3 generate : {} records", i + 1);
                } else if (i == totalDocGenerate - 1) {
                    log.info("full generate :  : {} records", totalDocGenerate);
                }
            } catch (InterruptedException e ) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.info("step {} exception. Ignore and continue", i);
                i--;
            }
            i++;
        } while (i < totalDocGenerate);
    }
}

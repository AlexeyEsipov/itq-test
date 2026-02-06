package ru.itq.util.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.itq.util.dto.DocCreateDto;

@Service
@Slf4j
public class GenerateService {

    @Value("${count.generated.docs:10}")
    private int totalDocGenerate;
    private static final String ACTION_BY = "generator";
    private final FeignService feignService;

    public GenerateService(FeignService feignService) {
        this.feignService = feignService;
    }

    @Async
    public void createDoc() {
        log.info("Start generate {} docs", totalDocGenerate);
        int i = 0;
        int firstThird = totalDocGenerate/3;
        int secondThird = totalDocGenerate*2/3;
        do {
            try {
                ResponseEntity<Long> response =
                feignService.createDocument(
                        new DocCreateDto(RandomWordGenerator.generateRandomWord(), "title-generate", ACTION_BY)
                );
                if (response == null || !response.hasBody() || response.getBody() < 1) {
                    i--;
                    continue;
                }
                if (i == firstThird) {
                    log.info("1/3 generate");
                } else if (i == secondThird) {
                    log.info("2/3 generate");
                } else if (i == totalDocGenerate-1) {
                    log.info("full generate");
                }
            } catch (Exception e) {
                log.info("step {} exception. Ignore and continue", i);
                i--;
            }
            i++;
        } while (i < totalDocGenerate);
    }
}

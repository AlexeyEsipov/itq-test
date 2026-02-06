package ru.itq.util.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import ru.itq.util.dto.DocBatch;
import ru.itq.util.dto.ResultAttempt;
import ru.itq.util.dto.DocCreateDto;

import java.util.List;

@FeignClient(name = "reestr", url = "${reestr.url}")
public interface FeignService {

    @PostMapping("/reestr/")
    ResponseEntity<Long> createDocument(DocCreateDto createDto);

    @PostMapping("/reestr/submit")
    ResponseEntity<List<ResultAttempt>> batchSubmit(DocBatch batch);

    @PostMapping("/reestr/approve")
    ResponseEntity<List<ResultAttempt>> batchApprove(DocBatch batch);

}

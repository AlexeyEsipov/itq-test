package ru.itq.reestr.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itq.reestr.dto.*;
import ru.itq.reestr.openapi.DocApi;
import ru.itq.reestr.service.DocumentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reestr")
public class DocController implements DocApi {
    private final DocumentService documentService;

    @PostMapping("/")
    public ResponseEntity<Long> createDocument(@RequestBody @Valid DocCreateDto createDto) {
        Long id = documentService.createDoc(createDto);
        return ResponseEntity.status(201).body(id);
    }

    @GetMapping("/{innerId}")
    public ResponseEntity<DocHistory> get(@PathVariable(value = "innerId") String innerId) {
        DocHistory docDto = documentService.getById(innerId);
        return ResponseEntity.ok(docDto);
    }

    @GetMapping("/")
    public ResponseEntity<Page<DocDto>> getAll(@ModelAttribute BathRequest bathRequest) {
        Page<DocDto> docDto = documentService.getAll(bathRequest);
        return ResponseEntity.ok(docDto);
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<DocDtoStatus>> findWithFilter(@ModelAttribute @Valid SearchValue searchValue) {
        Page<DocDtoStatus> result = documentService.findWithFilter(searchValue);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/submit")
    public ResponseEntity<List<ResultAttempt>> batchSubmit(@RequestBody DocBatch batch) {
        List<ResultAttempt> result = documentService.tryBatchSubmit(batch);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/approve")
    public ResponseEntity<List<ResultAttempt>> batchApprove(@RequestBody DocBatch batch) {
        List<ResultAttempt> result = documentService.tryBatchApprove(batch);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/approvecon")
    public ResponseEntity<ConcurrentResult> concurrentApprove(@RequestBody ConcurrentTestRequest request) {
        ConcurrentResult result = documentService.concurrentApproved(request);
        return ResponseEntity.ok(result);
    }
}

package ru.itq.reestr.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itq.reestr.check.WhiteColumnList;
import ru.itq.reestr.dto.*;
import ru.itq.reestr.exception.DocumentNotFoundException;
import ru.itq.reestr.mapper.DocStatusMapper;
import ru.itq.reestr.model.*;
import ru.itq.reestr.repository.DocRepository;
import ru.itq.reestr.repository.DocRepositoryCustom;
import ru.itq.reestr.repository.HistoryRepository;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentService {
    @Value("${default.page.number:0}")
    private int pageNumberDefault;

    @Value("${default.page.size:10}")
    private int pageSizeDefault;

    private final DocRepository docRepository;
    private final HistoryRepository historyRepository;
    private final DocRepositoryCustom docRepositoryCustom;
    private final WhiteColumnList whiteColumnList;
    private final DocumentDao documentDao;
    private final DocStatusMapper docStatusMapper;
    private final CreateSpecificationService createSpecification;

    @Transactional
    public Long createDoc(DocCreateDto createDto) {
        DocEntity doc = DocEntity.builder()
                .innerId(createDto.innerId())
                .title(createDto.title())
                .status(Status.DRAFT)
                .createdBy(createDto.createdBy())
                .createdAt(OffsetDateTime.now())
                .build();
        return docRepository.save(doc).getId();
    }

    public DocHistory getById(String innerId) {
        List<DocDtoStatus> byId = docRepository.findDtoById(innerId);
        if (byId.isEmpty()) {
            String message = "Document id : %s not found".formatted(innerId);
            log.info("{}", message);
            throw new DocumentNotFoundException(message);
        }
        DocDtoStatus doc = byId.get(0);
        List<HistoryDto> allById = historyRepository.findDtoAllByDocId(doc.id());
        return new DocHistory(doc, allById);
    }

    public Page<DocDto> getAll(BathRequest bathRequest) {
        List<String> innerIds = bathRequest.innerIds();
        if (innerIds == null || innerIds.isEmpty()) {
            return Page.empty(PageRequest.of(0,1));
        }
        int size = bathRequest.size();
        int pageNumber = bathRequest.page();
        String sortField = whiteColumnList.getDocsColumn(bathRequest.sortField());
        String sortDirection = bathRequest.sortDirection();
        String direction = (sortDirection == null
                || sortDirection.isBlank()
                || "desc".equalsIgnoreCase(sortDirection.trim()))
                ? "desc"
                : "asc";
        Pageable pageable = PageRequest.of(pageNumber, size);
        return docRepositoryCustom.findByInnerIdsAny(innerIds, pageable, sortField, direction);
    }

    public Page<DocDtoStatus> findWithFilter(SearchValue searchValue) {
        Filter filter = searchValue.filter();
        PageParam pageParam = searchValue.pageParam();
        PageRequest pageable = createPageRequest(pageParam);
        Specification<DocEntity> specification = createSpecification.createSpecification(filter);
        Page<DocEntity> page = docRepository.findAll(specification, pageable);
        return page.map(docStatusMapper::toDto);
    }

    public List<ResultAttempt> tryBatchSubmit(DocBatch batch) {
        String actionBy = batch.actionBy();
        List<String> innerIds = batch.docInnerIds();
        List<ResultAttempt> result = new ArrayList<>();
        for (String innerId : innerIds) {
            DocDtoStatus docDtoStatus = getDocDtoStatus(actionBy, result, innerId, Status.DRAFT);
            if (docDtoStatus == null) {
                continue;
            }
            Long docId = docDtoStatus.id();
            History history = createHistory(actionBy, docId, Action.SUBMIT);
            try {
                documentDao.submitProcess(docDtoStatus.id(), actionBy, history);
                result.add(new ResultAttempt(innerId, StatusAttempt.SUCCESS, actionBy));
            } catch (Exception e) {
                e.printStackTrace();
                result.add(new ResultAttempt(innerId, StatusAttempt.CONFLICT, actionBy));
            }
        }
        return result;
    }

    public List<ResultAttempt> tryBatchApprove(DocBatch batch) {
        String actionBy = batch.actionBy();
        List<String> innerIds = batch.docInnerIds();
        List<ResultAttempt> result = new ArrayList<>();
        for (String innerId : innerIds) {
            DocDtoStatus docDtoStatus = getDocDtoStatus(actionBy, result, innerId, Status.SUBMITTED);
            if (docDtoStatus == null) {
                continue;
            }
            Long docId = docDtoStatus.id();
            History history = createHistory(actionBy, docId, Action.APPROVE);
            DocApproval approval = createApproval(actionBy, docId, ApprovalDecision.APPROVED);
            try {
                documentDao.approvalProcess(docDtoStatus.id(), actionBy, history, approval);
                result.add(new ResultAttempt(innerId, StatusAttempt.SUCCESS, actionBy));
            } catch (Exception e) {
                e.printStackTrace();
                result.add(new ResultAttempt(innerId, StatusAttempt.ERROR, actionBy));
            }
        }
        return result;
    }

    public ConcurrentResult concurrentApproved(ConcurrentTestRequest request) {
        String innerId = request.innerId();
        Integer threads = request.threads();
        Integer threadAttempts = request.attempts();
        CountDownLatch latch = new CountDownLatch(threads);
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        List<Future<ResultAttemptConcurrent>> futures = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            Callable<ResultAttemptConcurrent> task = approve(innerId, latch, threadAttempts);
            futures.add(executorService.submit(task));
        }
        List<ResultAttemptConcurrent> attempts = new ArrayList<>();
        for (Future<ResultAttemptConcurrent> future : futures) {
            try {
                attempts.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
            }
        }
        executorService.shutdown();
        return convert(attempts) ;
    }

    private Callable<ResultAttemptConcurrent> approve(String innerId, CountDownLatch latch, Integer threadAttempts) {
        return () -> {
            latch.countDown();
            String name = Thread.currentThread().getName();
            int i = 0;
            ResultAttempt result;
            Integer successStep = 0;
            Integer conflictStep = 0;
            Integer errorStep = 0;
            Integer notFoundStep = 0;
            StatusAttempt statusAttempt;
            try {
                latch.await();
                do {
                    result = approveDoc1(innerId);
                    i++;
                    statusAttempt = result.status();
                    switch (statusAttempt) {
                        case CONFLICT -> conflictStep++;
                        case SUCCESS -> successStep++;
                        case ERROR -> errorStep++;
                        default -> notFoundStep++;
                    }
                } while (!result.status().equals(StatusAttempt.SUCCESS) && i < threadAttempts);
                return new ResultAttemptConcurrent(result, successStep, conflictStep, errorStep, notFoundStep, i);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return new ResultAttemptConcurrent(new ResultAttempt(innerId, StatusAttempt.ERROR, name),
                        successStep, conflictStep, errorStep, notFoundStep, i);
            }
        };
    }

    private History createHistory(String actionBy, Long docId, Action action) {
        return History.builder()
                .action(action)
                .actionBy(actionBy)
                .actionAt(OffsetDateTime.now())
                .description("descr")
                .docId(docId)
                .build();
    }

    public ResultAttempt approveDoc1(String innerId) {
        String actionBy = Thread.currentThread().getName();
        List<DocDtoStatus> dtoStatuses = docRepository.findDtoById(innerId);
        if (dtoStatuses.isEmpty()) {
            return new ResultAttempt(innerId, StatusAttempt.NOT_FOUND, actionBy);
        }
        if (dtoStatuses.size() > 1 ) {
            return new ResultAttempt(innerId, StatusAttempt.CONFLICT, actionBy);
        }
        DocDtoStatus docDtoStatus = dtoStatuses.get(0);
        if (!docDtoStatus.status().equals(Status.SUBMITTED)) {
            return new ResultAttempt(innerId, StatusAttempt.CONFLICT, actionBy);
        }
        Long docId = docDtoStatus.id();
        History history = createHistory(actionBy, docId, Action.APPROVE);
        DocApproval approval = createApproval(actionBy, docId, ApprovalDecision.APPROVED);
        try {
            if (documentDao.approvalProcess(docDtoStatus.id(), actionBy, history, approval)) {
                return new ResultAttempt(innerId, StatusAttempt.SUCCESS, actionBy);
            } else {
                return new ResultAttempt(innerId, StatusAttempt.ERROR, actionBy);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultAttempt(innerId, StatusAttempt.ERROR, actionBy);
        }
    }

    private DocDtoStatus getDocDtoStatus(String actionBy, List<ResultAttempt> result, String innerId, Status status) {
        List<DocDtoStatus> dtoStatuses = docRepository.findDtoById(innerId);
        if (dtoStatuses.isEmpty()) {
            result.add(new ResultAttempt(innerId, StatusAttempt.NOT_FOUND, actionBy));
            return null;
        }
        if (dtoStatuses.size() > 1 ) {
            result.add(new ResultAttempt(innerId, StatusAttempt.CONFLICT, actionBy));
            return null;
        }
        DocDtoStatus docDtoStatus = dtoStatuses.get(0);
        if (!docDtoStatus.status().equals(status)) {
            result.add(new ResultAttempt(innerId, StatusAttempt.CONFLICT, actionBy));
            return null;
        }
        return docDtoStatus;
    }

    private DocApproval createApproval(String actionBy, Long docId, ApprovalDecision approvalDecision) {
        return DocApproval.builder()
                .decision(approvalDecision)
                .innerId(docId)
                .approvedAt(OffsetDateTime.now())
                .approver(actionBy)
                .build();
    }

    private PageRequest createPageRequest(PageParam pageParam) {
        String sortDirection = pageParam.sortDirection();
        Sort.Direction direction = (sortDirection == null
                || sortDirection.isBlank()
                || "asc".equals(sortDirection.trim()))
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, pageParam.sortField(), "id");
        Integer pageNumber = pageParam.pageNumber();
        Integer pageSize = pageParam.pageSize();
        pageNumber = pageNumber == null ? pageNumberDefault : pageNumber;
        pageSize = pageSize == null ? pageSizeDefault : pageSize;
        return PageRequest.of(pageNumber, pageSize, sort);
    }

    private ConcurrentResult convert(List<ResultAttemptConcurrent> attempts) {
        String innerId = "";
        String winnerName = "";
        int totalAttempt = 0;
        int totalError = 0;
        int totalNotFoundStep = 0;
        int totalConflict = 0;
        int totalStep = 0;
        int totalSuccess = 0;
        ResultAttempt resultAttempt;
        for (ResultAttemptConcurrent attempt : attempts) {
            resultAttempt = attempt.attempt();
            innerId = resultAttempt.innerId();
            totalError = totalError + attempt.errorStep();
            totalNotFoundStep = totalNotFoundStep + attempt.notFoundStep();
            totalConflict = totalConflict + attempt.conflictStep();
            Integer successStep = attempt.successStep();
            if (successStep == 1) {
                winnerName = attempt.attempt().threadName();
            }
            totalSuccess = totalSuccess + successStep;
            totalStep = totalStep + attempt.step();
            totalAttempt = totalAttempt + attempt.errorStep() + attempt.notFoundStep()
                    + attempt.conflictStep() + attempt.successStep();
        }
        Status status = docRepository.findStatusByInnerId(innerId)
                .stream()
                .findFirst()
                .map(DocIdStatus::status)
                .orElseThrow(() -> new IllegalStateException("Status not found"));
        return new ConcurrentResult(innerId, winnerName, totalAttempt, totalError, totalNotFoundStep,
                totalConflict, totalSuccess, totalStep, status);
    }
}

package ru.itq.util.tasks.executors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import ru.itq.util.dto.DocBatch;
import ru.itq.util.model.Status;
import ru.itq.util.repository.DocRepository;
import ru.itq.util.service.ReestrService;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class DocApproveExecutor {
    @Value("${count.batchsize.docs:100}")
    private int batchSize;
    private final DocRepository docRepository;
    private final ReestrService reestrService;


    public void execute() {
        try {
            Page<String> dtoPage = docRepository.findPageDtoInnerIdByStatus(
                    Status.SUBMITTED,
                    PageRequest.of(0, batchSize)
            );
            long totalElements = dtoPage.getTotalElements();
            List<String> dtoStatusByInnerId = dtoPage.getContent();
            if (!dtoStatusByInnerId.isEmpty()) {
                DocBatch batch = new DocBatch(dtoStatusByInnerId, "util-approve", "batch-comment");
                reestrService.batchApprove(batch);
                log.info("task:docApprove - TotalDocuments: {} , send:  {} ", totalElements, dtoStatusByInnerId.size());
            } else {
                log.info("task:docApprove - TotalDocuments: {} ", totalElements);
            }
        } catch (Exception e) {
            log.info("Error docApprove: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

package ru.itq.reestr.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.itq.reestr.model.DocApproval;
import ru.itq.reestr.model.History;
import ru.itq.reestr.model.Status;
import ru.itq.reestr.repository.ApprovalRepository;
import ru.itq.reestr.repository.DocRepository;
import ru.itq.reestr.repository.HistoryRepository;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class DocumentDao {
    private final DocRepository docRepository;
    private final HistoryRepository historyRepository;
    private final ApprovalRepository approvalRepository;

    @Transactional
    public void submitProcess(Long id, String actionBy, History history) {
        docRepository.updateStatusAndMeta(id, Status.SUBMITTED, actionBy, OffsetDateTime.now());
        historyRepository.save(history);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean approvalProcess(Long id, String actionBy, History history, DocApproval docApproval) {
        int updated = docRepository.updateStatusIfMatch(
                id,
                Status.SUBMITTED,
                Status.APPROVED,
                actionBy,
                OffsetDateTime.now()
        );
        if (updated == 0) {
            return false;
        }
        historyRepository.save(history);
        DocApproval approval = approvalRepository.save(docApproval);
        if (approval.getId() == null) {
            throw new RuntimeException();
        }
        return true;
    }
}


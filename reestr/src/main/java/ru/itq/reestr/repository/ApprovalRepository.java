package ru.itq.reestr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itq.reestr.model.DocApproval;

public interface ApprovalRepository extends JpaRepository<DocApproval, Long> {
}

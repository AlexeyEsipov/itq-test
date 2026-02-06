package ru.itq.reestr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.itq.reestr.dto.HistoryDto;
import ru.itq.reestr.model.History;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {

    @Query("SELECT new ru.itq.reestr.dto.HistoryDto(h.id, h.docId, h.action, h.actionBy, h.actionAt, h.description) FROM History h WHERE h.docId = :docId")
    List<HistoryDto> findDtoAllByDocId(Long docId);
}

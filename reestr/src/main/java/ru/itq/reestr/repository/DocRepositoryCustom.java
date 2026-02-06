package ru.itq.reestr.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.itq.reestr.dto.DocDto;
import ru.itq.reestr.model.DocEntity;

import java.util.List;

public interface DocRepositoryCustom {

    Page<DocDto> findByInnerIdsAny(
            List<String> ids,
            Pageable pageable,
            String sortField,
            String direction
    );
}


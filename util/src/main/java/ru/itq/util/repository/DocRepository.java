package ru.itq.util.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.itq.util.model.DocEntity;
import ru.itq.util.model.Status;

import java.util.List;

public interface DocRepository extends JpaRepository<DocEntity, Long>, JpaSpecificationExecutor<DocEntity> {

    @Query("""
            SELECT d.innerId
                FROM DocEntity d
                WHERE d.status = :status""")
    List<String> findDtoInnerIdByStatus(@Param("status") Status status, Pageable pageable);

    @Query("""
            SELECT d.innerId
                FROM DocEntity d
                WHERE d.status = :status""")
    Page<String> findPageDtoInnerIdByStatus(@Param("status") Status status, Pageable pageable);
}

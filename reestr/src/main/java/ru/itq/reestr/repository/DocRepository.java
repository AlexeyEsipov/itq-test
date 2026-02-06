package ru.itq.reestr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.itq.reestr.dto.DocDtoStatus;
import ru.itq.reestr.dto.DocIdStatus;
import ru.itq.reestr.model.DocEntity;
import ru.itq.reestr.model.Status;

import java.time.OffsetDateTime;
import java.util.List;

public interface DocRepository extends JpaRepository<DocEntity, Long>, JpaSpecificationExecutor<DocEntity> {


    @Query("""
            SELECT new ru.itq.reestr.dto.DocDtoStatus(
                        d.id, d.innerId, d.title, d.status, d.createdBy, d.createdAt, d.updatedBy, d.updatedAt)
                FROM DocEntity d
                WHERE d.innerId = :innerId""")
    List<DocDtoStatus> findDtoById(@Param("innerId") String innerId);

    @Query("""
            SELECT new ru.itq.reestr.dto.DocIdStatus(d.id, d.innerId, d.status)
                FROM DocEntity d
                WHERE d.innerId = :innerId""")
    List<DocIdStatus> findStatusByInnerId(@Param("innerId") String innerId);

    @Modifying
    @Query("""
            UPDATE DocEntity d
                SET d.status = :status, d.updatedBy = :updatedBy, d.updatedAt = :updatedAt
                WHERE d.id = :id""")
    void updateStatusAndMeta(@Param("id") Long id,
                            @Param("status") Status status,
                            @Param("updatedBy") String updatedBy,
                            @Param("updatedAt") OffsetDateTime updatedAt);

    @Modifying
    @Query("""
    update DocEntity d
       set d.status = :newStatus,
           d.updatedBy = :updatedBy,
           d.updatedAt = :updatedAt
     where d.id = :id
       and d.status = :expectedStatus""")
    int updateStatusIfMatch(
            @Param("id") Long id,
            @Param("expectedStatus") Status expectedStatus,
            @Param("newStatus") Status newStatus,
            @Param("updatedBy") String updatedBy,
            @Param("updatedAt") OffsetDateTime updatedAt
    );
}

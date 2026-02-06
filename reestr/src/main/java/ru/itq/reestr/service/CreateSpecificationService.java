package ru.itq.reestr.service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.itq.reestr.dto.Filter;
import ru.itq.reestr.model.DocEntity;
import ru.itq.reestr.model.Status;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CreateSpecificationService {

    public Specification<DocEntity> createSpecification(Filter filter) {
        Status status = filter.status();
        OffsetDateTime createdAtTo = filter.createdAtTo();
        OffsetDateTime createdAtFrom = filter.createdAtFrom();
        OffsetDateTime updatedAtTo = filter.updatedAtTo();
        OffsetDateTime updatedAtFrom = filter.updatedAtFrom();
        String createdBy = filter.createdBy();
        return (root, query, cb) -> {
            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (createdBy != null) {
                predicates.add(cb.like(cb.upper(root.get("createdBy")), "%"+createdBy.toUpperCase().trim()+"%"));
            }

            if (createdAtFrom != null && createdAtTo != null) {
                predicates.add(
                        cb.and(
                                cb.greaterThanOrEqualTo(root.get("createdAt"), createdAtFrom),
                                cb.lessThanOrEqualTo(root.get("createdAt"), createdAtTo)));
            } else if (createdAtFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), createdAtFrom));
            } else if (createdAtTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), createdAtTo));
            }

            if (updatedAtFrom != null && updatedAtTo != null) {
                predicates.add(
                        cb.and(
                                cb.greaterThanOrEqualTo(root.get("updatedAt"), updatedAtFrom),
                                cb.lessThanOrEqualTo(root.get("updatedAt"), updatedAtTo)));
            } else if (updatedAtFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("updatedAt"), updatedAtFrom));
            } else if (updatedAtTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("updatedAt"), updatedAtTo));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

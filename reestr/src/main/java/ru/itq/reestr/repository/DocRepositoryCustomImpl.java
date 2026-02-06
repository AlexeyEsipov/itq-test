package ru.itq.reestr.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.itq.reestr.dto.DocDto;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DocRepositoryCustomImpl implements DocRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public Page<DocDto> findByInnerIdsAny(
            List<String> ids,
            Pageable pageable,
            String sortField,
            String direction) {
        if (ids == null || ids.isEmpty()) {
            return Page.empty(pageable);
        }
        String sql = """
                        SELECT *
                        FROM docs
                        WHERE inner_id = ANY (?)
                        ORDER BY %s %s
                        LIMIT ? OFFSET ?
                    """.formatted(sortField, direction);
        String countSql = """
                            SELECT count(*)
                            FROM docs
                            WHERE inner_id = ANY (?)
                        """;
        return entityManager.unwrap(Session.class)
                .doReturningWork(connection -> {
                    Array sqlArray = connection.createArrayOf("text", ids.toArray());
                    try (PreparedStatement ps = connection.prepareStatement(sql)) {
                        ps.setArray(1, sqlArray);
                        ps.setInt(2, pageable.getPageSize());
                        ps.setLong(3, pageable.getOffset());
                        ResultSet rs = ps.executeQuery();
                        List<DocDto> content = mapDocs(rs);
                        long total;
                        try (PreparedStatement cps = connection.prepareStatement(countSql)) {
                            cps.setArray(1, sqlArray);
                            ResultSet crs = cps.executeQuery();
                            crs.next();
                            total = crs.getLong(1);
                        }
                        return new PageImpl<>(content, pageable, total);
                    }
                });
    }

    private List<DocDto> mapDocs(ResultSet rs) throws SQLException {
        List<DocDto> list = new ArrayList<>();
        while (rs.next()) {
            DocDto d = new DocDto(
                    rs.getLong("id"),
                    rs.getString("inner_id"),
                    rs.getString("title"),
                    rs.getString("status"),
                    rs.getString("created_by"),
                    rs.getObject("created_at", OffsetDateTime.class),
                    rs.getString("updated_by"),
                    rs.getObject("updated_at", OffsetDateTime.class)
            );
            list.add(d);
        }
        return list;
    }
}

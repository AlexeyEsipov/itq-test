package ru.itq.reestr.check;

import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class WhiteColumnList {
    private static final Map<String, String> docsColumn = Map.of(
            "innerId", "inner_id",
            "title", "title",
            "status", "status",
            "createdBy", "created_by",
            "createdAt", "created_at",
            "updatedBy", "created_by",
            "updatedAt", "created_at"
            );

    public String getDocsColumn(String property) {
        String column = docsColumn.get(property);
        return column == null ? "id" : column;
    }
}

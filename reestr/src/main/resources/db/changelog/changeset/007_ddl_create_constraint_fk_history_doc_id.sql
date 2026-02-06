--liquibase formatted sql
--changeset esipov:create_constraint_fk_history_doc_id.sql
ALTER TABLE histories
    ADD CONSTRAINT histories_fk_doc_id
        FOREIGN KEY (doc_id)
    REFERENCES docs (id);

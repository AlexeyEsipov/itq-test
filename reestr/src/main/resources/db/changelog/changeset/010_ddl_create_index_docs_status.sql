--liquibase formatted sql
--changeset esipov:create_index_idx_docs_status.sql
CREATE INDEX idx_docs_status ON docs (status);
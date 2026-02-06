--liquibase formatted sql
--changeset esipov:create_index_idx_docs_created_at.sql
CREATE INDEX idx_docs_created_at ON docs (created_at);